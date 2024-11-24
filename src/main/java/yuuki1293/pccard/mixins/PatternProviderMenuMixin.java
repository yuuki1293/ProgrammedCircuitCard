package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.IPatternProviderMenuMixin;

@Mixin(value = PatternProviderMenu.class, remap = false)
public abstract class PatternProviderMenuMixin extends AEBaseMenu implements IPatternProviderMenuMixin {
    @Unique
    private IUpgradeableObject programmedCircuitCard$host;
    @Unique
    @GuiSync(8)
    public boolean programmedCircuitCard$pcMode = false;

    public PatternProviderMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V", at = @At("RETURN"))
    private void init(MenuType<? extends PatternProviderMenu> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        this.programmedCircuitCard$host = (IUpgradeableObject) host;

        this.programmedCircuitCard$setupUpgrades();
    }

    @Unique
    public void programmedCircuitCard$setupUpgrades() {
        setupUpgrades(this.programmedCircuitCard$getHost().getUpgrades());
    }

    @Unique
    public boolean programmedCircuitCard$getPCMode() {
        return this.programmedCircuitCard$pcMode;
    }

    @Unique
    public IUpgradeableObject programmedCircuitCard$getHost() {
        return this.programmedCircuitCard$host;
    }

    @Unique
    public IUpgradeInventory programmedCircuitCard$getUpgrades() {
        return programmedCircuitCard$getHost().getUpgrades();
    }

    @Unique
    public boolean programmedCircuitCard$hasUpgrade(ItemLike upgradeCard) {
        return programmedCircuitCard$getUpgrades().isInstalled(upgradeCard);
    }
}
