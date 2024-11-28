package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.AEBaseMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(value = AEBaseMenu.class, remap = false)
public class AEBaseMenuMixin {
    @Unique
    private List<IUpgradeInventory> pCCard$cache = new LinkedList<>();

    @Inject(method = "setupUpgrades", at = @At("HEAD"), cancellable = true)
    void setupUpgrades(IUpgradeInventory upgrades, CallbackInfo ci) {
        if (pCCard$cache.contains(upgrades))
            ci.cancel();
        else
            pCCard$cache.add(upgrades);
    }
}
