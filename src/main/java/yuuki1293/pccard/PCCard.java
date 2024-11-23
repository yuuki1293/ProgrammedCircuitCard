package yuuki1293.pccard;

import appeng.api.ids.AECreativeTabIds;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(PCCard.MODID)
public class PCCard {
    public static final String MODID = "pccard";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> PROGRAMMED_CIRCUIT_CARD_ITEM = ITEMS.register("card_programmed_circuit", () -> new Item(new Item.Properties()));

    public PCCard() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onBuildCreativeModeTabContentsEvent);

        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AECreativeTabIds.MAIN)) {
            event.accept(PROGRAMMED_CIRCUIT_CARD_ITEM);
            LOGGER.debug("Add Programmed Circuit Card in AE2 creative tab");
        }
    }
}
