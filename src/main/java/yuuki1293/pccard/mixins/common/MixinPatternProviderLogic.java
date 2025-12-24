package yuuki1293.pccard.mixins.common;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import appeng.api.crafting.IPatternDetails;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class MixinPatternProviderLogic implements IUpgradeableObject {

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @ModifyArg(
        method = "updatePatterns",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/crafting/PatternDetailsHelper;decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;"))
    private ItemStack updatePatterns(ItemStack stack) {
        if (!isUpgradedWith(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) return stack;

        return PatternProviderLogicImpl.updatePatterns(stack);
    }

    @Inject(
        method = "pushPattern",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/helpers/patternprovider/PatternProviderLogic;onPushPatternSuccess(Lappeng/api/crafting/IPatternDetails;)V"),
        require = 2)
    private void pushPattern(CallbackInfoReturnable<Boolean> cir,
        @Local(ordinal = 0, argsOnly = true) IPatternDetails patternDetails, @Local(ordinal = 0) Direction direction) {
        if (!isUpgradedWith(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) return;

        var be = this.host.getBlockEntity();
        var blockPoses = PatternProviderLogicImpl.getSendPos(be, direction);
        PatternProviderLogicImpl.setPCNumber(patternDetails, be, blockPoses);
    }
}
