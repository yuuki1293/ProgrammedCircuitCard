package yuuki1293.pccard;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PCCard.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigClient {

    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec spec;

    private static final ForgeConfigSpec.BooleanValue JEI_INTEGRATION;

    static {
        builder.comment("*****************************************");
        builder.comment("* Programmed Circuit Card Client Config *");
        builder.comment("*****************************************");

        JEI_INTEGRATION = builder.comment("Place a Programmed Circuit at the Pattern Encoding Terminal.")
            .define("jei_integration", true);

        spec = builder.build();
    }

    public static boolean getJeiIntegration() {
        return JEI_INTEGRATION.get();
    }
}
