package yuuki1293.pccard;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PCCard.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigCommon {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec spec;

    private static final ForgeConfigSpec.IntValue SEARCH_DEPTH;

    static {
        builder.comment("*****************************************");
        builder.comment("* Programmed Circuit Card Common Config *");
        builder.comment("*****************************************");

        SEARCH_DEPTH = builder
            .comment("Maximum depth for searching connected machines in subnet tree.")
            .comment("Higher values allow deeper traversal but may impact performance.")
            .defineInRange("search_depth", 5, 0, 100);

        spec = builder.build();
    }

    public static int getSearchDepth() {
        return SEARCH_DEPTH.get();
    }
}
