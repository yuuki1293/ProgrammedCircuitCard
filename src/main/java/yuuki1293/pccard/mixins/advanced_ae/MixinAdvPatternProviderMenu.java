package yuuki1293.pccard.mixins.advanced_ae;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.wrapper.IPatternProviderMenuMixin;

@Mixin(value = AdvPatternProviderMenu.class, remap = false)
public class MixinAdvPatternProviderMenu extends AEBaseMenu implements IPatternProviderMenuMixin {

    @Unique
    private IUpgradeableObject pCCard$host;

    @Unique
    ToolboxMenu pCCard$toolbox;

    public MixinAdvPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;)V",
        at = @At("TAIL"))
    private void init(MenuType<?> menuType, int id, Inventory playerInventory, AdvPatternProviderLogicHost host,
        CallbackInfo ci) {
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
        setupUpgrades(
            this.pCCard$getHost()
                .getUpgrades());
    }

    @Unique
    public IUpgradeableObject pCCard$getHost() {
        return this.pCCard$host;
    }

    @Unique
    public IUpgradeInventory pCCard$getUpgrades() {
        return pCCard$getHost().getUpgrades();
    }

    @Override
    public ToolboxMenu pCCard$getToolbox() {
        return this.pCCard$toolbox;
    }

    @Override
    protected ItemStack transferStackToMenu(ItemStack stack) {
        if (stack.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) {
            return pCCard$getUpgrades().addItems(stack);
        }
        return super.transferStackToMenu(stack);
    }
}
