package yuuki1293.pccard;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEParts;
import yuuki1293.pccard.api.PatternProviderRegistration;

@Mod(PCCard.MODID)
public class PCCard {

    public static final String MODID = "pccard";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> PROGRAMMED_CIRCUIT_CARD_ITEM = ITEMS
        .register("card_programmed_circuit", () -> Upgrades.createUpgradeCardItem(new Item.Properties()));

    public PCCard(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::onBuildCreativeModeTabContentsEvent);
        modEventBus.addListener(this::onAddPackFindersEvent);
        modEventBus.addListener(this::commonSetup);

        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        context.registerConfig(ModConfig.Type.CLIENT, ConfigClient.spec);
        context.registerConfig(ModConfig.Type.COMMON, ConfigCommon.spec);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(this::postRegistrationInitialization)
            .whenComplete((res, err) -> {
                if (err != null) {
                    LOGGER.warn(err.getMessage());
                }
            });
    }

    public void postRegistrationInitialization() {
        PatternProviderRegistration.register(AEParts.PATTERN_PROVIDER);
        PatternProviderRegistration.register(AEBlocks.PATTERN_PROVIDER);
    }

    @SubscribeEvent
    public void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (
            event.getTabKey()
                .equals(AECreativeTabIds.MAIN)
        ) {
            event.accept(PROGRAMMED_CIRCUIT_CARD_ITEM);
            LOGGER.debug("Add Programmed Circuit Card in AE2 creative tab");
        }
    }

    @SubscribeEvent
    public void onAddPackFindersEvent(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        var resourcePath = ModList.get()
            .getModFileById(MODID)
            .getFile()
            .findResource("resourcepacks/pccard_modern");
        var pack = Pack.readMetaAndCreate(
            "builtin/pccard_modern",
            Component.literal("Modern Texture"),
            false,
            (path) -> new PathPackResources(path, resourcePath, true),
            PackType.CLIENT_RESOURCES,
            Pack.Position.TOP,
            PackSource.BUILT_IN);
        event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
    }
}
