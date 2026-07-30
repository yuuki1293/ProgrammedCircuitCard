package yuuki1293.pccard.impl;

import java.util.Set;

public final class PatternBufferBlockingPolicy {

    private PatternBufferBlockingPolicy() {}

    public static <T> boolean full(Set<T> buffered) {
        return !buffered.isEmpty();
    }

    public static <T> boolean normal(Set<T> buffered, Set<T> incoming) {
        return buffered.stream()
            .anyMatch(incoming::contains);
    }

    public static <T> boolean smart(Set<T> buffered, Set<T> incoming) {
        return buffered.stream()
            .anyMatch(key -> !incoming.contains(key));
    }

    public static boolean circuitConflict(boolean hasBufferedPayload, int activeCircuit, int wantedCircuit) {
        return hasBufferedPayload && activeCircuit != wantedCircuit;
    }
}
