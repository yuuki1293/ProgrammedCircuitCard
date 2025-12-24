package yuuki1293.pccard.mixins.common;

import static yuuki1293.pccard.NBTs.NBT_CIRCUIT;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = PatternDetailsHelper.class, remap = false)
public class MixinPatternDetailsHelper {

    /**
     * set pc number
     */
    @Inject(
        method = "decodePattern(Lappeng/api/stacks/AEItemKey;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;",
        at = @At(value = "RETURN", ordinal = 0))
    private static void decodePattern(AEItemKey what, Level level, CallbackInfoReturnable<IPatternDetails> cir) {
        pCCard$decodePattern(what.getTag(), cir);
    }

    @Inject(
        method = "decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Z)Lappeng/api/crafting/IPatternDetails;",
        at = @At(value = "RETURN", ordinal = 0))
    private static void decodePattern(ItemStack stack, Level level, boolean autoRecovery,
        CallbackInfoReturnable<IPatternDetails> cir) {
        pCCard$decodePattern(stack.getTag(), cir);
    }

    @Unique
    private static void pCCard$decodePattern(CompoundTag tag, CallbackInfoReturnable<IPatternDetails> cir) {
        var ret = cir.getReturnValue();
        if (ret instanceof IAEPattern pattern) {
            if (tag != null && tag.contains(NBT_CIRCUIT)) {
                var number = tag.getInt(NBT_CIRCUIT);
                pattern.pCCard$setNumber(number);
            }
        }
    }
}
