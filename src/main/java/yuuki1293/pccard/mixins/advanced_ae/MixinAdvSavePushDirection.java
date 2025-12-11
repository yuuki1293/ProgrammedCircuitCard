package yuuki1293.pccard.mixins.advanced_ae;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AdvPatternProviderLogic.class, remap = false)
public abstract class MixinAdvSavePushDirection {
    @Unique
    private static Direction pCCard$sendDirection;

    @Inject(
            method = "pushPattern",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogic;onPushPatternSuccess(Lappeng/api/crafting/IPatternDetails;)V"),
            require = 2)
    private void saveDirection(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) Direction direction) {
        pCCard$sendDirection = direction;
    }

    @Inject(
            method = "pushInputsDirectionally",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogic;onPushPatternSuccess(Lappeng/api/crafting/IPatternDetails;)V"),
            require = 1)
    private void saveDirectionDirectionally(
            CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) Direction direction) {
        pCCard$sendDirection = direction;
    }
}
