package yuuki1293.pccard.mixins.common;

import static yuuki1293.pccard.NBTs.NBT_CIRCUIT;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.execution.ExecutingCraftingJob;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = ExecutingCraftingJob.class, remap = false)
public abstract class MixinExecutingCraftingJob {
    @Inject(
            method = "writeToNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putLong(Ljava/lang/String;J)V"))
    public void writeToNBT(
            CallbackInfoReturnable<CompoundTag> cir,
            @Local(name = "e") Map.Entry<IPatternDetails, ?> e,
            @Local(name = "item") CompoundTag item) {
        var key = e.getKey();
        if (key instanceof IAEPattern pattern) {
            // 0 is the default. can ignore.
            if (pattern.pCCard$getNumber() != 0) {
                item.putInt(NBT_CIRCUIT, pattern.pCCard$getNumber());
            }
        }
    }
}
