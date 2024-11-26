package yuuki1293.pccard;

import appeng.client.gui.widgets.UpgradesPanel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WidgetUtils {
    public static UpgradesPanel merge(UpgradesPanel u1, UpgradesPanel u2) {
        try {
            var slotsField = UpgradesPanel.class.getDeclaredField("slots");
            var tooltipSupplierField = UpgradesPanel.class.getDeclaredField("tooltipSupplier");
            slotsField.setAccessible(true);
            tooltipSupplierField.setAccessible(true);

            @SuppressWarnings("unchecked")
            var slots1 = (List<Slot>) slotsField.get(u1);
            @SuppressWarnings("unchecked")
            var slots2 = (List<Slot>) slotsField.get(u2);
            var newSlots = Stream.concat(slots1.stream(), slots2.stream()).toList();

            @SuppressWarnings("unchecked")
            var tooltipSupplier1 = (Supplier<List<Component>>) tooltipSupplierField.get(u1);
            @SuppressWarnings("unchecked")
            var tooltipSupplier2 = (Supplier<List<Component>>) tooltipSupplierField.get(u2);
            Supplier<List<Component>> newTooltipSupplier = () -> Stream.concat(
                tooltipSupplier1.get().stream(),
                tooltipSupplier2.get().stream()
            ).toList();

            return new UpgradesPanel(newSlots, newTooltipSupplier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
