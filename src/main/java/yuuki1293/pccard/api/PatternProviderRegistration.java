package yuuki1293.pccard.api;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.upgrades.Upgrades;
import yuuki1293.pccard.PCCard;

/**
 * Registers pattern-provider host items as compatible with the Programmed Circuit Card.
 *
 * <p>
 * Providers based on AE2's standard pattern-provider logic are registered automatically.
 * Providers with custom logic can call this method before creating their upgrade inventory.
 */
public final class PatternProviderRegistration {

    private static final Set<Item> REGISTERED_ITEMS = Collections.newSetFromMap(new IdentityHashMap<>());

    private PatternProviderRegistration() {}

    public static synchronized void register(ItemLike provider) {
        if (provider == null) return;

        var item = provider.asItem();
        if (item == null || !REGISTERED_ITEMS.add(item)) return;

        Upgrades.add(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get(), item, 1, item.getDescriptionId());
    }
}
