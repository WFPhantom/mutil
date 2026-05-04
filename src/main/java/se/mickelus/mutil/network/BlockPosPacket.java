package se.mickelus.mutil.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

public abstract class BlockPosPacket extends AbstractPacket {

    protected BlockPos pos;

    public BlockPosPacket() {}

    public BlockPosPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }
}