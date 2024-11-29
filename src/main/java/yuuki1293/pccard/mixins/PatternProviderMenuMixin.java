package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.CompetitionFixer;
import yuuki1293.pccard.IPatternProviderMenuMixin;

@Mixin(value = PatternProviderMenu.class, remap = false)
public abstract class PatternProviderMenuMixin extends AEBaseMenu implements IPatternProviderMenuMixin {
    @Unique
    private IUpgradeableObject pCCard$host;

    public PatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V", at = @At("TAIL"))
    private void init(int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        this.pCCard$host = (IUpgradeableObject) host;
        this.pCCard$setupUpgrades();
    }

    @Unique
    public void pCCard$setupUpgrades() {
        setupUpgrades(this.pCCard$getHost().getUpgrades());
    }

    @Unique
    public IUpgradeableObject pCCard$getHost() {
        return this.pCCard$host;
    }

    @Unique
    public IUpgradeInventory pCCard$getUpgrades() {
        return pCCard$getHost().getUpgrades();
    }

}
