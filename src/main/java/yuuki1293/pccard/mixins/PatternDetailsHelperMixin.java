package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = PatternDetailsHelper.class, remap = false)
public class PatternDetailsHelperMixin {
    /**
     * set pc number
     */
    @Inject(method = "decodePattern(Lappeng/api/stacks/AEItemKey;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;", at = @At(value = "RETURN", ordinal = 0))
    private static void decodePattern(AEItemKey what, Level level, CallbackInfoReturnable<IPatternDetails> cir) {
        pCCard$decodePattern(what.get(PCCard.RECIPE_CIRCUIT.get()), cir);
    }

    @Inject(method = "decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;", at = @At(value = "RETURN", ordinal = 0))
    private static void decodePattern(ItemStack stack, Level level, CallbackInfoReturnable<IPatternDetails> cir) {
        pCCard$decodePattern(stack.get(PCCard.RECIPE_CIRCUIT.get()), cir);
    }

    @Unique
    private static void pCCard$decodePattern(Integer number, CallbackInfoReturnable<IPatternDetails> cir) {
        var ret = cir.getReturnValue();
        if (ret instanceof IAEPattern pattern
            && number != null) {
            pattern.pCCard$setNumber(number);
        }
    }
}
