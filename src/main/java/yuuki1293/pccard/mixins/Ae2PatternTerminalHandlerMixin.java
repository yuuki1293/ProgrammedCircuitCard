package yuuki1293.pccard.mixins;

import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.integration.emi.recipe.Ae2PatternTerminalHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.PCCard;

import java.util.List;
import java.util.stream.Stream;

@Mixin(value = Ae2PatternTerminalHandler.class, remap = false)
public abstract class Ae2PatternTerminalHandlerMixin {
    @Shadow
    private static List<GenericStack> intoGenericStack(EmiIngredient ingredient) {
        return null;
    }

    @Inject(method = "ofInputs", at = @At("RETURN"), cancellable = true)
    private static void ofInputs(EmiRecipe emiRecipe, CallbackInfoReturnable<List<List<GenericStack>>> cir) {
        var inputs = cir.getReturnValue();

        var circuitStack = EmiStack.of(GTItems.INTEGRATED_CIRCUIT);
        var circuit = emiRecipe.getCatalysts().stream().filter(ei -> EmiIngredient.areEqual(ei, circuitStack)).findFirst();

        if (circuit.isPresent()) {
            var stack = GenericStack.fromItemStack(circuit.get().getEmiStacks().get(0).getItemStack());
            if (stack == null) {
                PCCard.LOGGER.error("can't find generic stack");
            } else {
                var ret = Stream.concat(inputs.stream(), Stream.of(List.of(stack))).toList();
                cir.setReturnValue(ret);
            }
        }
    }
}
