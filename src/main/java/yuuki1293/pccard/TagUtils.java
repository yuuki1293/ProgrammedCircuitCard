package yuuki1293.pccard;

import appeng.api.ids.AEComponents;
import com.gregtechceu.gtceu.data.item.GTDataComponents;
import net.minecraft.world.item.ItemStack;

public class TagUtils {
    /**
     * get inputs itemStacks from Pattern.<br>
     * circuit will be deleted.
     * return 0 ~ 31
     */
    public static int extractCircuitNumber(ItemStack stack) {
        var component = stack.get(AEComponents.ENCODED_PROCESSING_PATTERN);
        if(component == null) return -1;

        var inputs = component.sparseInputs();

        for (int i = 0; i < inputs.size(); i++) {
            var input = inputs.get(i);
            var number = input.what().get(GTDataComponents.CIRCUIT_CONFIG.get());

            if(number != null) {
                inputs.remove(i);
                return number;
            }
        }

        return -1;
    }
}
