package yuuki1293.pccard.mixins;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fml.loading.FMLLoader;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Controls load/unloading of Mixin classes.
 * When unloading and loading conflict, unloading takes priority.
 */
public class PCCardMixinPlugin implements IMixinConfigPlugin {

    private static final String COMMON_MIXIN_PACKAGE = "yuuki1293.pccard.mixins.common.";
    private static final Map<String, Set<String>> LOAD_WHEN_MOD_PRESENT = new HashMap<>();
    private static final Map<String, Set<String>> EXCLUDE_WHEN_MOD_PRESENT = new HashMap<>();
    private static final Set<String> EXCLUDE_ALWAYS = new HashSet<>();
    private static final String DISABLE_SLOT_PROPERTY = "pccard.disableSlot";

    private static Set<String> jvmArguments = Set.of();

    static {
        LOAD_WHEN_MOD_PRESENT.put(
            "appflux",
            Set.of(
                "yuuki1293.pccard.mixins.appflux.MixinAppFluxAddUpgradeSlot",
                "yuuki1293.pccard.mixins.appflux.MixinAppFluxAdvAddUpgradeSlot"));
        EXCLUDE_WHEN_MOD_PRESENT.put(
            "appflux",
            Set.of(
                "yuuki1293.pccard.mixins.common.MixinAddUpgradeSlot",
                "yuuki1293.pccard.mixins.common.MixinPatternProviderScreen",
                "yuuki1293.pccard.mixins.common.MixinPatternProviderMenu",
                "yuuki1293.pccard.mixins.common.MixinPatternProviderLogicHost",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvAddUpgradeSlot",
                "yuuki1293.pccard.mixins.advanced_ae.MixinSmallAdvPatternProviderScreen",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderMenu",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderLogicHost",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderScreen"));

        LOAD_WHEN_MOD_PRESENT.put(
            "advanced_ae",
            Set.of(
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvAddUpgradeSlot",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderLogic",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderLogicHost",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderMenu",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderScreen",
                "yuuki1293.pccard.mixins.advanced_ae.MixinAdvProcessingPattern",
                "yuuki1293.pccard.mixins.advanced_ae.MixinSmallAdvPatternProviderScreen"));
    }

    @Override
    public void onLoad(String mixinPackage) {
        jvmArguments = gatherJvmArguments();
        if (shouldSkipAddUpgradesSlotMixins()) {
            EXCLUDE_ALWAYS.addAll(
                Set.of(
                    "yuuki1293.pccard.mixins.common.MixinAddUpgradeSlot",
                    "yuuki1293.pccard.mixins.common.MixinPatternProviderScreen",
                    "yuuki1293.pccard.mixins.common.MixinPatternProviderMenu",
                    "yuuki1293.pccard.mixins.common.MixinPatternProviderLogicHost",
                    "yuuki1293.pccard.mixins.advanced_ae.MixinAdvAddUpgradeSlot",
                    "yuuki1293.pccard.mixins.advanced_ae.MixinSmallAdvPatternProviderScreen",
                    "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderMenu",
                    "yuuki1293.pccard.mixins.advanced_ae.MixinAdvPatternProviderLogicHost"));
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Default: only load common mixins
        boolean load = mixinClassName.startsWith(COMMON_MIXIN_PACKAGE);

        // Check if mixin should only load when specific mod is present
        if (!load) {
            for (Map.Entry<String, Set<String>> entry : LOAD_WHEN_MOD_PRESENT.entrySet()) {
                String requiredModId = entry.getKey();
                Set<String> mixinsForMod = entry.getValue();
                if (mixinsForMod.contains(mixinClassName)) {
                    if (
                        FMLLoader.getLoadingModList()
                            .getModFileById(requiredModId) != null
                    ) {
                        load = true;
                    }
                }
            }
        }

        // Check if mixin should be excluded when specific mod is present
        if (load) {
            for (Map.Entry<String, Set<String>> entry : EXCLUDE_WHEN_MOD_PRESENT.entrySet()) {
                String conflictingModId = entry.getKey();
                Set<String> mixinsToExclude = entry.getValue();
                if (mixinsToExclude.contains(mixinClassName)) {
                    if (
                        FMLLoader.getLoadingModList()
                            .getModFileById(conflictingModId) != null
                    ) {
                        load = false;
                    }
                }
            }
        }

        // Check if mixin should be excluded
        if (load) {
            if (EXCLUDE_ALWAYS.contains(mixinClassName)) {
                load = false;
            }
        }

        return load;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static Set<String> gatherJvmArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return new HashSet<>(runtimeMXBean.getInputArguments());
    }

    private static boolean shouldSkipAddUpgradesSlotMixins() {
        if (Boolean.getBoolean(DISABLE_SLOT_PROPERTY)) {
            return true;
        }

        return hasJvmArgument("-D" + DISABLE_SLOT_PROPERTY);
    }

    private static boolean hasJvmArgument(String argument) {
        return jvmArguments.stream()
            .anyMatch(arg -> arg.equals(argument) || arg.startsWith(argument + "="));
    }
}
