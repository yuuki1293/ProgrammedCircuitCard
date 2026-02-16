package yuuki1293.pccard;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import appeng.core.localization.GuiText;

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
        var patternProviderGroup = GuiText.CraftingInterface.getTranslationKey();
        var item = PROGRAMMED_CIRCUIT_CARD_ITEM.get();

        // AE2 Pattern Provider
        Upgrades.add(item, AEParts.PATTERN_PROVIDER, 1, patternProviderGroup);
        Upgrades.add(item, AEBlocks.PATTERN_PROVIDER, 1, patternProviderGroup);

        // Extended AE Pattern Provider
        var exPatternProviderGroup = "gui.expatternprovider.ex_pattern_provider";
        var resourceExBE = ResourceLocation.fromNamespaceAndPath("expatternprovider", "ex_pattern_provider");
        var resourceExPart = ResourceLocation.fromNamespaceAndPath("expatternprovider", "ex_pattern_provider_part");
        var patternProviderExBE = ForgeRegistries.BLOCKS.getValue(resourceExBE);
        var patternProviderExPart = ForgeRegistries.ITEMS.getValue(resourceExPart);
        if (patternProviderExBE != null && patternProviderExPart != null) {
            Upgrades.add(item, patternProviderExBE, 1, exPatternProviderGroup);
            Upgrades.add(item, patternProviderExPart, 1, exPatternProviderGroup);
        }

        // Advanced AE Pattern Provider
        {
            var adPatternProviderGroup = "gui.advanced_ae.AdvPatternProvider";
            var namespaceAd = "advanced_ae";
            var resourceAdBE = ResourceLocation.fromNamespaceAndPath(namespaceAd, "small_adv_pattern_provider");
            var resourceAdPart = ResourceLocation.fromNamespaceAndPath(namespaceAd, "small_adv_pattern_provider_part");
            var resourceAdExBE = ResourceLocation.fromNamespaceAndPath(namespaceAd, "adv_pattern_provider");
            var resourceAdExPart = ResourceLocation.fromNamespaceAndPath(namespaceAd, "adv_pattern_provider_part");
            var patternProviderAdBE = ForgeRegistries.BLOCKS.getValue(resourceAdBE);
            var patternProviderAdPart = ForgeRegistries.ITEMS.getValue(resourceAdPart);
            var patternProviderAdExBE = ForgeRegistries.BLOCKS.getValue(resourceAdExBE);
            var patternProviderAdExPart = ForgeRegistries.ITEMS.getValue(resourceAdExPart);
            if (
                patternProviderAdBE != null && patternProviderAdPart != null
                    && patternProviderAdExBE != null
                    && patternProviderAdExPart != null
            ) {
                Upgrades.add(item, patternProviderAdBE, 1, adPatternProviderGroup);
                Upgrades.add(item, patternProviderAdPart, 1, adPatternProviderGroup);
                Upgrades.add(item, patternProviderAdExBE, 1, adPatternProviderGroup);
                Upgrades.add(item, patternProviderAdExPart, 1, adPatternProviderGroup);
            }
        }

        // Expanded AE Pattern Provider
        {
            var expPatternProviderGroup = "gui.expandedae.exp_pattern_provider";
            var namespaceExp = "expandedae";
            var resourceExpBE = ResourceLocation.fromNamespaceAndPath(namespaceExp, "exp_pattern_provider");
            var resourceExpPart = ResourceLocation.fromNamespaceAndPath(namespaceExp, "exp_pattern_provider_part");
            var patternProviderExpBE = ForgeRegistries.BLOCKS.getValue(resourceExpBE);
            var patternProviderExpPart = ForgeRegistries.ITEMS.getValue(resourceExpPart);
            if (patternProviderExpBE != null && patternProviderExpPart != null) {
                Upgrades.add(item, patternProviderExpBE, 1, expPatternProviderGroup);
                Upgrades.add(item, patternProviderExpPart, 1, expPatternProviderGroup);
            }
        }

        // Mega Cells Pattern Provider
        {
            var megaPatternProviderGroup = "block.megacells.mega_pattern_provider";
            var namespaceMega = "megacells";
            var resourceMegaBE = ResourceLocation.fromNamespaceAndPath(namespaceMega, "mega_pattern_provider");
            var resourceMegaPart = ResourceLocation.fromNamespaceAndPath(namespaceMega, "cable_mega_pattern_provider");
            var patternProviderMegaBE = ForgeRegistries.BLOCKS.getValue(resourceMegaBE);
            var patternProviderMegaPart = ForgeRegistries.ITEMS.getValue(resourceMegaPart);
            if (patternProviderMegaBE != null && patternProviderMegaPart != null) {
                Upgrades.add(item, patternProviderMegaBE, 1, megaPatternProviderGroup);
                Upgrades.add(item, patternProviderMegaPart, 1, megaPatternProviderGroup);
            }
        }
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
