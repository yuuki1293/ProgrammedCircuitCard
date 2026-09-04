package yuuki1293.pccard.mixins.expandedae;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.util.ConfigManager;
import lu.kolja.expandedae.api.patternprovider.IHighlightable;
import lu.kolja.expandedae.api.patternprovider.IPatternProviderLogic;
import lu.kolja.expandedae.api.patternprovider.PatternProviderTargetCache;
import lu.kolja.expandedae.definition.ExpSettings;
import lu.kolja.expandedae.enums.BlockingMode;

/**
 * Restores the ExpandedAE adapter path that ExpandedAE deliberately skips when PCCard is present.
 */
@Mixin(value = PatternProviderLogic.class, remap = false, priority = 1090)
public abstract class MixinExpandedPatternProviderLogic implements IPatternProviderLogic, IHighlightable {

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow
    @Final
    private IActionSource actionSource;

    @Shadow
    @Final
    private ConfigManager configManager;

    @Unique
    private PatternProviderTargetCache[] pCCard$targetCaches;

    @Inject(
        method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
        at = @At("TAIL"))
    private void pCCard$initializeExpandedAdapter(IManagedGridNode mainNode, PatternProviderLogicHost host,
        int patternInventorySize, CallbackInfo ci) {
        pCCard$targetCaches = new PatternProviderTargetCache[Direction.values().length];
        configManager.registerSetting(ExpSettings.BLOCKING_MODE, BlockingMode.DEFAULT);
    }

    @Inject(method = "findAdapter", at = @At("HEAD"), cancellable = true)
    private void pCCard$findExpandedAdapter(Direction side, CallbackInfoReturnable<PatternProviderTarget> cir) {
        var index = side.get3DDataValue();
        if (pCCard$targetCaches[index] == null) {
            var blockEntity = host.getBlockEntity();
            pCCard$targetCaches[index] = new PatternProviderTargetCache(
                (ServerLevel) blockEntity.getLevel(),
                blockEntity.getBlockPos()
                    .relative(side),
                side.getOpposite(),
                actionSource,
                configManager);
        }
        cir.setReturnValue(pCCard$targetCaches[index].find());
    }

    @Override
    public BlockingMode expandedae$getBlockingMode() {
        return configManager.getSetting(ExpSettings.BLOCKING_MODE);
    }

    @Override
    public BlockEntity eae$getBlockPos() {
        return host.getBlockEntity();
    }
}
