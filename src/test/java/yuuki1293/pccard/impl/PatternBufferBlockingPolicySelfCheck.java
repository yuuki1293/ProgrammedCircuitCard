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

        require(
            !ExpandedPatternProviderTarget.smartBlocks(Set.of(), Set.of(), Set.of(), Set.of()),
            "Smart mode must allow an empty target");
        require(
            !ExpandedPatternProviderTarget.smartBlocks(Set.of("iron"), Set.of("iron"), Set.of(), Set.of()),
            "Smart mode must allow matching recipe inputs");
        require(
            ExpandedPatternProviderTarget.smartBlocks(Set.of("iron", "gold"), Set.of("iron"), Set.of(), Set.of()),
            "Smart mode must block foreign recipe inputs");
        require(
            !ExpandedPatternProviderTarget.smartBlocks(Set.of(), Set.of(), Set.of("circuit-4"), Set.of("circuit-4")),
            "Smart mode must allow an exact programmed-circuit match");
        require(
            ExpandedPatternProviderTarget.smartBlocks(Set.of(), Set.of(), Set.of("circuit-4"), Set.of("circuit-7")),
            "Smart mode must block a wrong-circuit-only buffer");
        require(
            ExpandedPatternProviderTarget
                .smartBlocks(Set.of("iron"), Set.of("iron"), Set.of("circuit-4", "circuit-7"), Set.of("circuit-4")),
            "Smart mode must require every buffered circuit");
        require(
            !ExpandedPatternProviderTarget.smartBlocks(
                Set.of("iron"),
                Set.of("iron"),
                Set.of("circuit-4", "circuit-7"),
                Set.of("circuit-4", "circuit-7")),
            "Smart mode must allow matching recipe inputs and circuits");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
