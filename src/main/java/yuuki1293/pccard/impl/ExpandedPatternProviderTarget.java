package yuuki1293.pccard.impl;

import java.util.HashSet;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.util.ConfigManager;
import lu.kolja.expandedae.definition.ExpSettings;
import lu.kolja.expandedae.enums.BlockingMode;

/**
 * Adds programmed-circuit identity to ExpandedAE's smart blocking without depending on its anonymous target class.
 */
public final class ExpandedPatternProviderTarget implements PatternProviderTarget {

    private final PatternProviderTarget delegate;
    private final MEStorage storage;
    private final ConfigManager configManager;

    public ExpandedPatternProviderTarget(PatternProviderTarget delegate, MEStorage storage,
        ConfigManager configManager) {
        this.delegate = delegate;
        this.storage = storage;
        this.configManager = configManager;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable type) {
        return delegate.insert(what, amount, type);
    }

    @Override
    public boolean containsPatternInput(Set<AEKey> patternInputs) {
        if (configManager.getSetting(ExpSettings.BLOCKING_MODE) != BlockingMode.SMART) {
            return delegate.containsPatternInput(patternInputs);
        }

        var bufferedInputs = new HashSet<AEKey>();
        var bufferedCircuits = new HashSet<AEKey>();
        splitKeys(
            storage.getAvailableStacks()
                .keySet(),
            bufferedInputs,
            bufferedCircuits);

        var incomingInputs = new HashSet<AEKey>();
        var incomingCircuits = new HashSet<AEKey>();
        splitKeys(patternInputs, incomingInputs, incomingCircuits);

        return smartBlocks(bufferedInputs, incomingInputs, bufferedCircuits, incomingCircuits);
    }

    private static void splitKeys(Iterable<AEKey> keys, Set<AEKey> inputs, Set<AEKey> circuits) {
        for (var key : keys) {
            if (PatternProviderLogicImpl.isProgrammedCircuit(key)) {
                circuits.add(key);
            } else {
                inputs.add(key.dropSecondary());
            }
        }
    }

    static <T> boolean smartBlocks(Set<T> bufferedInputs, Set<T> incomingInputs, Set<T> bufferedCircuits,
        Set<T> incomingCircuits) {
        return PatternBufferBlockingPolicy.smart(bufferedInputs, incomingInputs)
            || (!bufferedInputs.isEmpty() && !incomingCircuits.containsAll(bufferedCircuits));
    }
}
