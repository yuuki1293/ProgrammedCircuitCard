package yuuki1293.pccard.mixins.common;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import appeng.api.crafting.IPatternDetails;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class MixinPatternProviderLogic implements IUpgradeableObject, IPatternProviderLogicMixin {

    @Unique
    private static Direction pCCard$sendDirection;

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow
    private Direction sendDirection;

    @ModifyArg(
        method = "updatePatterns",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/crafting/PatternDetailsHelper;decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;"))
    private ItemStack updatePatterns(ItemStack stack) {
        return PatternProviderLogicImpl.updatePatterns(this, stack);
    }

    @Override
    public void pCCard$setPCNumber(IPatternDetails patternDetails) {
        PatternProviderLogicImpl.setPCNumber(this, patternDetails);
    }

    @Override
    public List<BlockPos> pCCard$getSendPos() {
        return PatternProviderLogicImpl.getSendPos(pCCard$getLevel(), this);
    }

    @Override
    public Direction pCCard$getSendDirection() {
        if (this.sendDirection == null) return pCCard$sendDirection;
        return this.sendDirection;
    }

    @Override
    public void pCCard$setSendDirection(Direction direction) {
        pCCard$sendDirection = direction;
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
    public Level pCCard$getLevel() {
        return pCCard$getBlockEntity().getLevel();
    }
}
