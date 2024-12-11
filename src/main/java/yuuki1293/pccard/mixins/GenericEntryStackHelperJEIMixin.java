package yuuki1293.pccard.mixins;

import appeng.api.stacks.GenericStack;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import com.gregtechceu.gtceu.common.data.GTItems;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.PCCard;

import java.util.List;
import java.util.stream.Stream;

@Mixin(value = GenericEntryStackHelper.class, remap = false)
public abstract class GenericEntryStackHelperJEIMixin {
    @Shadow
    private static List<GenericStack> ofSlot(IRecipeSlotView slot) {
        return null;
    }

    @Inject(method = "ofInputs", at = @At("TAIL"), cancellable = true)
    private static void ofInputs(IRecipeSlotsView recipeLayout, CallbackInfoReturnable<List<List<GenericStack>>> cir) {
        var inputs = cir.getReturnValue();

        var circuitStack = GTItems.INTEGRATED_CIRCUIT.asStack();
        var circuit = recipeLayout.getSlotViews(RecipeIngredientRole.CATALYST).stream()
            .filter(ei -> {
                var stack = ei.getDisplayedItemStack().orElse(ItemStack.EMPTY);
                return ItemStack.isSameItem(stack, circuitStack);
            })
            .findFirst();

        if (circuit.isPresent()) {
            var stack = ofSlot(circuit.get());
            if (stack == null) {
                PCCard.LOGGER.error("can't find generic stack");
            } else {
                var ret = Stream.concat(inputs.stream(), Stream.of(stack)).toList();
                cir.setReturnValue(ret);
            }
        }
    }
}
