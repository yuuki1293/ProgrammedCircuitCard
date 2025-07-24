package yuuki1293.pccard.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import yuuki1293.pccard.PCCard;

@EventBusSubscriber(modid = PCCard.MODID)
public class PCCDataGen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getLookupProvider();
        pack.addProvider(p -> new PCCRecipeProvider(p, lookup));
    }
}
