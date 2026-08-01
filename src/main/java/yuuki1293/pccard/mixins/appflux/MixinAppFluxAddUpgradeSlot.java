package yuuki1293.pccard.mixins.appflux;

import net.minecraft.world.level.ItemLike;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bawnorton.mixinsquared.TargetHandler;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import yuuki1293.pccard.api.PatternProviderRegistration;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 1100)
public abstract class MixinAppFluxAddUpgradeSlot implements IUpgradeableObject {

    @Shadow
    public abstract void updatePatterns();

    /**
     * expand upgrades slot
     */
    @TargetHandler(mixin = "com.glodblock.github.appflux.mixins.MixinPatternProviderLogic", name = "initUpgrade")
    @ModifyArg(
        method = "@MixinSquared:Handler",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/upgrades/UpgradeInventories;forMachine(Lnet/minecraft/world/level/ItemLike;ILappeng/api/upgrades/MachineUpgradesChanged;)Lappeng/api/upgrades/IUpgradeInventory;"),
        index = 0)
    private ItemLike registerProvider(ItemLike provider) {
        PatternProviderRegistration.register(provider);
        return provider;
    }

    @TargetHandler(mixin = "com.glodblock.github.appflux.mixins.MixinPatternProviderLogic", name = "initUpgrade")
    @ModifyArg(
        method = "@MixinSquared:Handler",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/upgrades/UpgradeInventories;forMachine(Lnet/minecraft/world/level/ItemLike;ILappeng/api/upgrades/MachineUpgradesChanged;)Lappeng/api/upgrades/IUpgradeInventory;"),
        index = 1)
    private int modifyMaxUpgrades(int maxUpgrades) {
        return maxUpgrades + 1;
    }

    /**
     * update patterns when set/unset pccard
     */
    @TargetHandler(
        mixin = "com.glodblock.github.appflux.mixins.MixinPatternProviderLogic",
        name = "af_$onUpgradesChanged")
    @Inject(method = "@MixinSquared:Handler", at = @At("RETURN"))
    @Unique
    private void af_$onUpgradesChanged(CallbackInfo ci) {
        updatePatterns();
    }
}
