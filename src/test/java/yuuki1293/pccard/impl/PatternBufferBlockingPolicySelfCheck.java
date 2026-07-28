package yuuki1293.pccard.impl;

import java.util.Set;

public final class PatternBufferBlockingPolicySelfCheck {

    private PatternBufferBlockingPolicySelfCheck() {}

    public static void main(String[] args) {
        require(!PatternBufferBlockingPolicy.full(Set.of()), "Full mode must allow an empty buffer");
        require(PatternBufferBlockingPolicy.full(Set.of("iron")), "Full mode must block buffered input");

        require(
            PatternBufferBlockingPolicy.normal(Set.of("iron"), Set.of("iron", "copper")),
            "Normal mode must block advertised buffered input");
        require(
            !PatternBufferBlockingPolicy.normal(Set.of("gold"), Set.of("iron", "copper")),
            "Normal mode must ignore input no installed pattern advertises");

        require(
            !PatternBufferBlockingPolicy.smart(Set.of("iron"), Set.of("iron", "copper")),
            "Smart mode must allow compatible repeated input");
        require(
            PatternBufferBlockingPolicy.smart(Set.of("iron", "gold"), Set.of("iron", "copper")),
            "Smart mode must block unrelated buffered input");

        require(
            !PatternBufferBlockingPolicy.circuitConflict(false, 4, 7),
            "An empty buffer must permit a circuit switch");
        require(
            !PatternBufferBlockingPolicy.circuitConflict(true, 4, 4),
            "Buffered input with the same circuit must remain compatible");
        require(
            PatternBufferBlockingPolicy.circuitConflict(true, 4, 7),
            "Buffered input must reject a different circuit");
        require(
            PatternBufferBlockingPolicy.circuitConflict(true, -2, 7),
            "Unknown recovered circuit state must fail closed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
