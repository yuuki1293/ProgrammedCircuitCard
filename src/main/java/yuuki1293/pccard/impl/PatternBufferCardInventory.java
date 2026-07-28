package yuuki1293.pccard.impl;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import yuuki1293.pccard.PCCard;

public final class PatternBufferCardInventory extends CustomItemStackHandler {

    private final BooleanSupplier canChangeCard;
    private final Runnable onCardChanged;
    private boolean cardInstalled;

    public PatternBufferCardInventory(BooleanSupplier canChangeCard, Runnable onCardChanged) {
        super(1);
        this.canChangeCard = canChangeCard;
        this.onCardChanged = onCardChanged;
        setFilter(stack -> stack.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()));
    }

    public void synchronizeCardState() {
        cardInstalled = !getStackInSlot(0).isEmpty();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canChangeCard.getAsBoolean() && super.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        var current = getStackInSlot(slot);
        var changesCard = !(current.isEmpty() && stack.isEmpty()) && !ItemStack.isSameItemSameTags(current, stack);
        if (changesCard && !canChangeCard.getAsBoolean()) return;
        super.setStackInSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!canChangeCard.getAsBoolean()) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canChangeCard.getAsBoolean()) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        var cardInstalledNow = !getStackInSlot(0).isEmpty();
        if (cardInstalled != cardInstalledNow) {
            cardInstalled = cardInstalledNow;
            onCardChanged.run();
        }
    }
}
