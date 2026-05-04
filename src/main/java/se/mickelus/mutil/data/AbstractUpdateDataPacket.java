package se.mickelus.mutil.data;


import com.google.gson.JsonElement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.network.AbstractPacket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public abstract class AbstractUpdateDataPacket extends AbstractPacket {
    protected String directory;
    protected Map<Identifier, String> data;

    public AbstractUpdateDataPacket() {}

    public AbstractUpdateDataPacket(String directory, Map<Identifier, JsonElement> data) {
        this.directory = directory;
        this.data = data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()
                ));
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(directory);
        buffer.writeInt(data.size());
        data.forEach((identifier, data) -> {
            buffer.writeIdentifier(identifier);
            buffer.writeUtf(data);
        });
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buffer) {
        directory = buffer.readUtf();
        int count = buffer.readInt();
        data = new HashMap<>();
        for (int i = 0; i < count; i++) {
            data.put(buffer.readIdentifier(), buffer.readUtf());
        }
    }

    @Override
    public abstract void handle(Player player);
}