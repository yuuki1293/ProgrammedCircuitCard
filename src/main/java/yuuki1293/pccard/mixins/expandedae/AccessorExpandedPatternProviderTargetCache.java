package yuuki1293.pccard.mixins.expandedae;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.util.ConfigManager;
import lu.kolja.expandedae.api.patternprovider.PatternProviderTargetCache;

@Mixin(value = PatternProviderTargetCache.class, remap = false)
public interface AccessorExpandedPatternProviderTargetCache {

    @Accessor("configManager")
    ConfigManager pCCard$getConfigManager();
}
