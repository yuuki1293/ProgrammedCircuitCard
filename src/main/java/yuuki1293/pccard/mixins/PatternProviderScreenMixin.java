package yuuki1293.pccard.mixins;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.IPatternProviderMenuMixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PatternProviderScreen.class, remap = false)
public class PatternProviderScreenMixin extends AEBaseScreen<PatternProviderMenu> {
    public PatternProviderScreenMixin(PatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        this.widgets.add("upgrades", new UpgradesPanel(
            menu.getSlots(SlotSemantics.UPGRADE),
            this::pCCard$getCompatibleUpgrades));
    }

    @Unique
    private List<Component> pCCard$getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(((IPatternProviderMenuMixin) menu).pCCard$getUpgrades().getUpgradableItem()));
        return list;
    }
}
