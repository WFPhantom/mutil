package se.mickelus.mutil.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemHandlerWrapper implements Container {

    protected final ResourceHandler<ItemResource> inv;

    public ItemHandlerWrapper(ResourceHandler<ItemResource> inv) {
        this.inv = inv;
    }

    /**
     * Returns the size of this inventory.
     */
    @Override
    public int getContainerSize() {
        return inv.size();
    }

    /**
     * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
     */
    @Override
    public ItemStack getItem(int slot) {
        return inv.getResource(slot).toStack(inv.getAmountAsInt(slot));
    }

    /**
     * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
     */
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemResource resource = inv.getResource(slot);
        if (resource.isEmpty()) return ItemStack.EMPTY;
        try (var tx = Transaction.openRoot()) {
            int extracted = inv.extract(slot, resource, count, tx);
            tx.commit();
            return resource.toStack(extracted);
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (stack.isEmpty()) return;
        try (var tx = Transaction.openRoot()) {
            inv.insert(slot, ItemResource.of(stack), stack.getCount(), tx);
            tx.commit();
        }
    }


    /**
     * Removes the stack contained in this slot from the underlying handler, and returns it.
     */
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = getItem(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(index, ItemStack.EMPTY);
        return s;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inv.size(); i++) {
            if (!inv.getResource(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return inv.isValid(slot, ItemResource.of(stack));
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inv.size(); i++) {
            ItemResource resource = inv.getResource(i);
            if (!resource.isEmpty()) {
                try (var tx = Transaction.openRoot()) {
                    inv.extract(i, resource, inv.getAmountAsInt(i), tx);
                    tx.commit();
                }
            }
        }
    }

    //The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
    @Override
    public int getMaxStackSize() { return 0; }
    @Override
    public void setChanged() {}
    @Override
    public boolean stillValid(Player player) { return false; }
}
