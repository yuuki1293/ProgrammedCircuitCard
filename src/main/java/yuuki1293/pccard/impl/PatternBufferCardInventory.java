package yuuki1293.pccard.impl;

import java.util.function.BooleanSupplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import yuuki1293.pccard.PCCard;

public final class PatternBufferCardInventory extends CustomItemStackHandler {

    private static final int SLOT_COUNT = 3;

    private final BooleanSupplier canChangeCard;
    private final Runnable onCardChanged;
    private boolean cardInstalled;

    public PatternBufferCardInventory(BooleanSupplier canChangeCard, Runnable onCardChanged) {
        super(SLOT_COUNT);
        this.canChangeCard = canChangeCard;
        this.onCardChanged = onCardChanged;
        setFilter(stack -> stack.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()));
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        var resized = tag.copy();
        resized.putInt("Size", SLOT_COUNT);
        super.deserializeNBT(resized);
    }

    public boolean hasCard() {
        for (int slot = 0; slot < getSlots(); slot++) {
            if (getStackInSlot(slot).is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) return true;
        }
        return false;
    }

    public void synchronizeCardState() {
        cardInstalled = hasCard();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canChangeCard.getAsBoolean() && super.isItemValid(slot, stack)
            && (getStackInSlot(slot).is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()) || !hasCard());
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        var current = getStackInSlot(slot);
        var changesCard = !(current.isEmpty() && stack.isEmpty()) && !ItemStack.isSameItemSameTags(current, stack);
        if (changesCard && !canChangeCard.getAsBoolean()) return;
        if (
            !stack.isEmpty() && !current.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())
                && stack.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())
                && hasCard()
        ) return;
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
        var cardInstalledNow = hasCard();
        if (cardInstalled != cardInstalledNow) {
            cardInstalled = cardInstalledNow;
            onCardChanged.run();
        }
    }
}
