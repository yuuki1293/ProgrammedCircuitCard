package yuuki1293.pccard;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEParts;
import appeng.core.localization.GuiText;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

    public static final RegistryObject<Item> PROGRAMMED_CIRCUIT_CARD_ITEM = ITEMS.register("card_programmed_circuit", () -> Upgrades.createUpgradeCardItem(new Item.Properties()));

    public PCCard() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onBuildCreativeModeTabContentsEvent);
        modEventBus.addListener(this::commonSetup);

        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(this::postRegistrationInitialization).whenComplete((res, err) -> {
            if (err != null) {
                LOGGER.warn(err.getMessage());
            }
        });
    }

    public void postRegistrationInitialization(){
        var patternProviderGroup = GuiText.CraftingInterface.getTranslationKey();
        var item = PROGRAMMED_CIRCUIT_CARD_ITEM.get();

        Upgrades.add(item, AEParts.PATTERN_PROVIDER, 1, patternProviderGroup);
        Upgrades.add(item, AEBlocks.PATTERN_PROVIDER, 1, patternProviderGroup);

        // Extended AE Pattern Provider
        var resourceBE = new ResourceLocation("expatternprovider", "ex_pattern_provider");
        var resourcePart = new ResourceLocation("expatternprovider", "ex_pattern_provider_part");
        var patternProviderBE = ForgeRegistries.BLOCKS.getValue(resourceBE);
        var patternProviderPart = ForgeRegistries.BLOCKS.getValue(resourcePart);
        if(patternProviderBE != null && patternProviderPart != null) {
            Upgrades.add(item, patternProviderBE, 1, patternProviderGroup);
            Upgrades.add(item, patternProviderPart, 1, patternProviderGroup);
        }
    }

    @SubscribeEvent
    public void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AECreativeTabIds.MAIN)) {
            event.accept(PROGRAMMED_CIRCUIT_CARD_ITEM);
            LOGGER.debug("Add Programmed Circuit Card in AE2 creative tab");
        }
    }
}
