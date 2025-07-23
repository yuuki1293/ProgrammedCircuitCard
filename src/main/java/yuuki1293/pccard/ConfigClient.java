package yuuki1293.pccard;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigClient {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue JEI_INTEGRATION;

    static {
        builder.comment("*****************************************");
        builder.comment("* Programmed Circuit Card Client Config *");
        builder.comment("*****************************************");

        JEI_INTEGRATION = builder
            .comment("Place a Programmed Circuit at the Pattern Encoding Terminal.")
            .define("jei_integration", true);

        SPEC = builder.build();
    }
}
