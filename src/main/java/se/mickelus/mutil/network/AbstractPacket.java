package se.mickelus.mutil.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractPacket {

    /**
     * Encode the packet data into the ByteBuf stream.
     *
     * @param buffer the buffer to encode into
     */
    public abstract void toBytes(RegistryFriendlyByteBuf buffer);

    /**
     * Decode the packet data from the ByteBuf stream.
     *
     * @param buffer the buffer to decode from
     */
    public abstract void fromBytes(RegistryFriendlyByteBuf buffer);

    /**
     * Handle the reception of this packet.
     *
     * @param player A reference to the sending player when handled on the server side
     */
    public abstract void handle(Player player);
}