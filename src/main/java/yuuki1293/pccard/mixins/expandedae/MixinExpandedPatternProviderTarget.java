package yuuki1293.pccard.mixins.expandedae;

import java.lang.reflect.Field;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import lu.kolja.expandedae.api.patternprovider.PatternProviderTargetCache;
import lu.kolja.expandedae.definition.ExpSettings;
import lu.kolja.expandedae.enums.BlockingMode;

@Mixin(targets = "lu.kolja.expandedae.api.patternprovider.PatternProviderTargetCache$1", remap = false)
public class MixinExpandedPatternProviderTarget {

    @Unique
    private static Field pCCard$storageField;
    @Unique
    private static Field pCCard$ownerField;

    @Inject(method = "containsPatternInput", at = @At("HEAD"), cancellable = true)
    private void compareProgrammedCircuit(Set<AEKey> patternInputs, CallbackInfoReturnable<Boolean> cir) {
        var storage = pCCard$getField("val$storage", MEStorage.class, true);
        var owner = pCCard$getField("this$0", PatternProviderTargetCache.class, false);
        if (storage == null || owner == null) return;

        var configManager = ((AccessorExpandedPatternProviderTargetCache) (Object) owner).pCCard$getConfigManager();
        if (configManager.getSetting(ExpSettings.BLOCKING_MODE) != BlockingMode.SMART) return;

        var hasRecipeInputs = false;
        var circuitMatches = true;
        for (var stack : storage.getAvailableStacks()) {
            var key = stack.getKey();
            if (
                key.getId()
                    .getNamespace()
                    .equals("gtceu")
                    && key.getId()
                        .getPath()
                        .equals("programmed_circuit")
            ) {
                circuitMatches = patternInputs.contains(key);
                continue;
            }
            hasRecipeInputs = true;
            if (!patternInputs.contains(key.dropSecondary())) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(hasRecipeInputs && !circuitMatches);
    }

    @Unique
    private <T> T pCCard$getField(String name, Class<T> type, boolean storageField) {
        try {
            var field = storageField ? pCCard$storageField : pCCard$ownerField;
            if (field == null) {
                field = getClass().getDeclaredField(name);
                field.setAccessible(true);
                if (storageField) pCCard$storageField = field;
                else pCCard$ownerField = field;
            }
            return type.cast(field.get(this));
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return null;
        }
    }
}
