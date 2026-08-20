package yuuki1293.pccard.mixins.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class PatternP2PDestinationTimingSelfCheck {

    private PatternP2PDestinationTimingSelfCheck() {}

    public static void main(String[] args) throws IOException {
        var classFile = MixinPatternP2PTunnelLogic.class.getResourceAsStream("MixinPatternP2PTunnelLogic.class");
        if (classFile == null) throw new AssertionError("Missing Pattern P2P mixin class file");

        var bytecode = new String(classFile.readAllBytes(), StandardCharsets.ISO_8859_1);
        require(bytecode.contains("ICraftingMachine;pushPattern"), "Missing nested Pattern P2P tracking injection");
        require(
            bytecode.contains("AFTER"),
            "Nested Pattern P2P destination is captured before the nested push selects its current output");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
