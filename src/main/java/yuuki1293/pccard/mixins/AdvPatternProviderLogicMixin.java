package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.xmod.CompetitionFixer;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;

import java.util.List;

@Mixin(value = AdvPatternProviderLogic.class, remap = false, priority = 800)
public abstract class AdvPatternProviderLogicMixin implements IUpgradeableObject, IPatternProviderLogicMixin {
    @Unique
    private static Direction pCCard$sendDirection;

    @Shadow
    public abstract void updatePatterns();

    @Shadow
    @Final
    private AdvPatternProviderLogicHost host;

    @Shadow
    private Direction sendDirection;
    @Unique
    private IUpgradeInventory pCCard$upgrades;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, AdvPatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) return;

        pCCard$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::pCCard$upgradesChange);
    }

    @Unique
    private void pCCard$upgradesChange() {
        this.host.saveChanges();
        updatePatterns();
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeToNBT(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) return;

        this.pCCard$upgrades.writeToNBT(tag, "upgrades", registries);
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readFromNBT(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) return;

        this.pCCard$upgrades.readFromNBT(tag, "upgrades", registries);
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return pCCard$upgrades;
    }

    @Inject(method = "addDrops", at = @At("HEAD"))
    private void addDrops(List<ItemStack> drops, CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) return;

        for (var is : this.pCCard$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    private void clearContent(CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) return;

        this.pCCard$upgrades.clear();
    }

    @ModifyArg(method = "updatePatterns", at = @At(value = "INVOKE", target = "Lappeng/api/crafting/PatternDetailsHelper;decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;"))
    private ItemStack updatePatterns(ItemStack stack) {
        return PatternProviderLogicImpl.updatePatterns(this, stack);
    }

    @Override
    public void pCCard$setPCNumber(IPatternDetails patternDetails) {
        PatternProviderLogicImpl.setPCNumber(this, patternDetails);
    }

    @Override
    public List<BlockPos> pCCard$getSendPos() {
        return PatternProviderLogicImpl.getSendPos(pCCard$getLevel(), this, AdvPatternProviderLogic.class);
    }

    @Override
    public Direction pCCard$getSendDirection() {
        if (this.sendDirection == null)
            return pCCard$sendDirection;
        return this.sendDirection;
    }

    @Override
    public boolean pCCard$hasPCCard() {
        return isUpgradedWith(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get());
    }

    @Override
    public BlockEntity pCCard$getBlockEntity() {
        return this.host.getBlockEntity();
    }

    @Unique
    private Level pCCard$getLevel() {
        return pCCard$getBlockEntity().getLevel();
    }

    // This is necessary?
    @Inject(method = "sendStacksOut", at = @At("HEAD"))
    private void sendStacksOut(CallbackInfoReturnable<Boolean> cir) {
        pCCard$sendDirection = this.sendDirection;
    }
}

