package yuuki1293.pccard.mixins;

import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.UpgradesPanel;
import com.google.common.base.Preconditions;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import yuuki1293.pccard.WidgetUtils;

import java.util.Map;

@Mixin(value = WidgetContainer.class, remap = false)
public abstract class WidgetContainerMixin {
    @Final
    @Shadow
    private ScreenStyle style;
    @Final
    @Shadow
    private Map<String, AbstractWidget> widgets;
    @Final
    @Shadow
    private Map<String, ICompositeWidget> compositeWidgets;

    /**
     * @author yuuki1293
     * @reason Because several mods try to add an upgrade widget to the ME Pattern Provider.
     */
    @Overwrite
    public void add(String id, ICompositeWidget widget) {
        Preconditions.checkState(!widgets.containsKey(id), "%s already used for widget", id);

        // Size the widget, as this doesn't change when the parent is resized
        WidgetStyle widgetStyle = style.getWidget(id);
        widget.setSize(widgetStyle.getWidth(), widgetStyle.getHeight());

        if (compositeWidgets.containsKey(id)) {
            if (id.equals("upgrades")) {
                var upgradeWidget = (UpgradesPanel) compositeWidgets.get("upgrades");
                var merged = WidgetUtils.merge(upgradeWidget, (UpgradesPanel) widget);
                compositeWidgets.replace("upgrades", merged);
            } else {
                throw new IllegalStateException("Duplicate id: " + id);
            }
        } else {
            compositeWidgets.put(id, widget);
        }
    }
}
