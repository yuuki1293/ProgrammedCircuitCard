package yuuki1293.pccard.mixins.advanced_ae;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.AdvPatternProviderScreen;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.wrapper.IPatternProviderMenuMixin;

@Mixin(value = AdvPatternProviderScreen.class, remap = false)
public abstract class MixinAdvPatternProviderScreen extends AEBaseScreen<AdvPatternProviderMenu> {
    public MixinAdvPatternProviderScreen(
            AdvPatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        this.widgets.add(
                "upgrades",
                new UpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE), this::pCCard$getCompatibleUpgrades));
        if (((IPatternProviderMenuMixin) menu).pCCard$getToolbox().isPresent()) {
            this.widgets.add(
                    "toolbox",
                    new ToolboxPanel(
                            style,
                            ((IPatternProviderMenuMixin) menu)
                                    .pCCard$getToolbox()
                                    .getName()));
        }
    }

    @Unique
    private List<Component> pCCard$getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(
                ((IPatternProviderMenuMixin) menu).pCCard$getUpgrades().getUpgradableItem()));
        return list;
    }
}
