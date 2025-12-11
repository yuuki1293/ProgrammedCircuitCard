package yuuki1293.pccard.mixins.common;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.wrapper.IPatternProviderMenuMixin;

@Mixin(value = PatternProviderMenu.class, remap = false)
public abstract class MixinPatternProviderMenu extends AEBaseMenu implements IPatternProviderMenuMixin {
    @Unique
    private IUpgradeableObject pCCard$host;

    @Unique
    private ToolboxMenu pCCard$toolbox;

    public MixinPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
            method =
                    "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
            at = @At("TAIL"))
    private void init(
            MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        this.pCCard$host = (IUpgradeableObject) host;
        this.pCCard$toolbox = new ToolboxMenu(this);
        this.pCCard$setupUpgrades();
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    public void tickToolbox(CallbackInfo ci) {
        this.pCCard$toolbox.tick();
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

    @Unique
    public ToolboxMenu pCCard$getToolbox() {
        return this.pCCard$toolbox;
    }
}
