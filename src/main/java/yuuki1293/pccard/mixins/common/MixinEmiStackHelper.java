package yuuki1293.pccard.mixins.common;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gregtechceu.gtceu.common.data.GTItems;

import appeng.api.stacks.GenericStack;
import appeng.integration.modules.emi.EmiStackHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import yuuki1293.pccard.ConfigClient;
import yuuki1293.pccard.PCCard;

@Mixin(value = EmiStackHelper.class, remap = false)
public abstract class MixinEmiStackHelper {

    @Inject(method = "ofInputs", at = @At("RETURN"), cancellable = true)
    private static void ofInputs(EmiRecipe emiRecipe, CallbackInfoReturnable<List<List<GenericStack>>> cir) {
        if (!ConfigClient.getJeiIntegration()) return; // config

        var inputs = cir.getReturnValue();

        var circuitStack = GTItems.PROGRAMMED_CIRCUIT.asStack();
        var circuit = emiRecipe.getCatalysts()
            .stream()
            .filter(ei -> {
                var stack = ei.getEmiStacks()
                    .get(0)
                    .getItemStack();
                return ItemStack.isSameItem(stack, circuitStack);
            })
            .findFirst();

        if (circuit.isPresent()) {
            var stack = GenericStack.fromItemStack(
                circuit.get()
                    .getEmiStacks()
                    .get(0)
                    .getItemStack());
            if (stack == null) {
                PCCard.LOGGER.error("can't find generic stack");
            } else {
                var ret = Stream.concat(inputs.stream(), Stream.of(List.of(stack)))
                    .toList();
                cir.setReturnValue(ret);
            }
        }
    }
}
