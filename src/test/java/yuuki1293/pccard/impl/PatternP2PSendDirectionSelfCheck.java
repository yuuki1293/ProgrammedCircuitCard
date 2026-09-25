package yuuki1293.pccard.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Documents the MAE2 Pattern P2P direction contract used by {@link PatternProviderLogicImpl#getSendPosDirect}.
 * <p>
 * MAE2 {@code Target.side()} is the insert face on the destination (AE2
 * {@code sendDirection.getOpposite()}). {@link PatternProviderLogicImpl#getSendPos} later applies
 * {@code getOpposite()} before {@code IPartHost.getPart}, so the P2P-tracked direction must be
 * converted to AE2 sendDirection first. Nested Pattern P2P copies that tracked insert face as-is;
 * conversion happens once here, not at mixin write time.
 */
public final class PatternP2PSendDirectionSelfCheck {

    private PatternP2PSendDirectionSelfCheck() {}

    public static void main(String[] args) throws IOException {
        var impl = readProjectFile("src/main/java/yuuki1293/pccard/impl/PatternProviderLogicImpl.java");
        require(
            impl.contains("return new Tuple<>(patternP2PPos, patternP2PDirection.getOpposite());"),
            "P2P-tracked direction must match AE2 sendDirection semantics before getSendPos applies getOpposite");

        var mixin = readProjectFile("src/main/java/yuuki1293/pccard/mixins/common/MixinPatternP2PTunnelLogic.java");
        require(
            mixin.contains("this.pCCard$lastDirection = output.side();"),
            "Pattern P2P mixin must record MAE2 Target.side() (insert face) unchanged");
        require(
            mixin.contains("this.pCCard$lastDirection = patternP2PTunnelLogicMixin.pCCard$getLastDirection();"),
            "Nested Pattern P2P must copy destination direction without an extra getOpposite");
        require(
            !mixin.contains("output.side().getOpposite()"),
            "Do not invert at mixin write time; nested P2P would be flipped twice");
    }

    private static String readProjectFile(String relativePath) throws IOException {
        var path = Path.of(relativePath);
        if (!Files.isRegularFile(path)) {
            throw new AssertionError(
                "Missing " + relativePath
                    + " (cwd="
                    + Path.of("")
                        .toAbsolutePath()
                    + ")");
        }
        return Files.readString(path);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
