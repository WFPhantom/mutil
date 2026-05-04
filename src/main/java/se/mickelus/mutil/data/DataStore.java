package se.mickelus.mutil.data;

import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class DataStore<V> extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    protected static final int jsonExtLength = ".json".length();
    private static final Logger logger = LogManager.getLogger();
    protected Gson gson;
    protected String namespace;
    protected String directory;
    protected Class<V> dataClass;
    protected Map<Identifier, JsonElement> rawData;
    protected Map<Identifier, V> dataMap;
    protected List<Runnable> listeners;
    private final DataDistributor syncronizer;

    public DataStore(Gson gson, String namespace, String directory, Class<V> dataClass, DataDistributor synchronizer) {
        this.gson = gson;
        this.namespace = namespace;
        this.directory = directory;

        this.dataClass = dataClass;
        this.syncronizer = synchronizer;

        rawData = Collections.emptyMap();
        dataMap = Collections.emptyMap();

        listeners = new LinkedList<>();
    }

    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        logger.debug("Reading data for {} data store...", directory);
        Map<Identifier, JsonElement> map = Maps.newHashMap();
        int i = this.directory.length() + 1;

        for (Map.Entry<Identifier, Resource> entry : resourceManager.listResources(directory, rl -> rl.getPath().endsWith(".json")).entrySet()) {
            if (!namespace.equals(entry.getKey().getNamespace())) {
                continue;
            }

            String path = entry.getKey().getPath();
            Identifier location = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), path.substring(i, path.length() - jsonExtLength));

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json;

                if (dataClass.isArray()) {
                    JsonArray sources = getSources(entry.getValue());
                    json = GsonHelper.fromJson(gson, reader, JsonArray.class);
                    json.getAsJsonArray().forEach(element -> {
                        if (element.isJsonObject()) {
                            element.getAsJsonObject().add("sources", sources);
                        }
                    });
                } else {
                    json = GsonHelper.fromJson(gson, reader, JsonElement.class);
                    json.getAsJsonObject().add("sources", getSources(entry.getValue()));
                }

                if (shouldLoad(json)) {
                    JsonElement duplicate = map.put(location, json);
                    if (duplicate != null) {
                        throw new IllegalStateException("Duplicate data ignored with ID " + location);
                    }
                } else {
                    logger.debug("Skipping data '{}' due to condition", entry.getKey());
                }
            } catch (IllegalArgumentException | IOException | JsonParseException exception) {
                logger.error("Couldn't parse data '{}' from '{}'", location, entry.getKey(), exception);
            }
        }

        return map;
    }

    protected JsonArray getSources(Resource resource) {
        String fileId = resource.sourcePackId();
        JsonArray result = new JsonArray();

        ModList.get().getModFiles().stream()
                .filter(modInfo -> fileId.equals(modInfo.getFile().getFileName()))
                .flatMap(fileInfo -> fileInfo.getMods().stream())
                .map(IModInfo::getDisplayName)
                .forEach(result::add);

        if (result.isEmpty()) {
            result.add(fileId);
        }

        return result;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> splashList, ResourceManager resourceManager, ProfilerFiller profiler) {
        rawData = splashList;

        // PacketHandler dependencies get upset when called upon before the server has started properly
        if (FMLEnvironment.getDist().isDedicatedServer() && ServerLifecycleHooks.getCurrentServer() != null) {
            syncronizer.sendToAll(directory, rawData);
        }

        parseData(rawData);
    }

    public void sendToPlayer(ServerPlayer player) {
        syncronizer.sendToPlayer(player, directory, rawData);
    }

    public void loadFromPacket(Map<Identifier, String> data) {
        Map<Identifier, JsonElement> splashList = data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            if (dataClass.isArray()) {
                                return GsonHelper.fromJson(gson, entry.getValue(), JsonArray.class);
                            } else {
                                return GsonHelper.fromJson(gson, entry.getValue(), JsonElement.class);
                            }
                        }
                ));

        parseData(splashList);
    }

    public void parseData(Map<Identifier, JsonElement> splashList) {
        logger.info("Loaded {} {}", String.format("%3d", splashList.size()), directory);
        dataMap = splashList.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> gson.fromJson(entry.getValue(), dataClass)
                ));

        processData();

        listeners.forEach(Runnable::run);
    }

    protected boolean shouldLoad(JsonElement json) {
        if (json.isJsonArray()) {
            JsonArray arr = json.getAsJsonArray();
            if (!arr.isEmpty()) {
                json = arr.get(0);
            }
        }

        if (!json.isJsonObject()) {
            return true;
        }

        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("conditions") && !jsonObject.has(ConditionalOps.DEFAULT_CONDITIONS_KEY)) {
            jsonObject = jsonObject.deepCopy();
            jsonObject.add(ConditionalOps.DEFAULT_CONDITIONS_KEY, jsonObject.remove("conditions"));
        }

        return ICondition.conditionsMatched(JsonOps.INSTANCE, jsonObject);
    }

    protected void processData() {

    }

    public Map<Identifier, JsonElement> getRawData() {
        return rawData;
    }

    public String getDirectory() {
        return directory;
    }

    /**
     * Get the resource at the given location from the set of resources that this listener is managing
     *
     * @param identifier A resource location
     * @return An object matching the type of this listener, or null if none exists at the given location
     */
    public V getData(Identifier identifier) {
        return dataMap.get(identifier);
    }

    /**
     * @return all data from this store.
     */
    public Map<Identifier, V> getData() {
        return dataMap;
    }

    /**
     * Get all resources (if any) that are within the directory denoted by the provided resource location
     *
     * @param identifier
     * @return
     */
    public Collection<V> getDataIn(Identifier identifier) {
        return getData().entrySet().stream()
                .filter(entry -> identifier.getNamespace().equals(entry.getKey().getNamespace())
                        && entry.getKey().getPath().startsWith(identifier.getPath()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Listen to changes on resources in this store
     *
     * @param callback A runnable that is to be called when the store is reloaded
     */
    public void onReload(Runnable callback) {
        listeners.add(callback);
    }
}
