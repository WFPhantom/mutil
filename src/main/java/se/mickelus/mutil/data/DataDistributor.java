package se.mickelus.mutil.data;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public interface DataDistributor {
    void sendToAll(String directory, Map<Identifier, JsonElement> dataMap);
    void sendToPlayer(ServerPlayer player, String directory, Map<Identifier, JsonElement> dataMap);
}
