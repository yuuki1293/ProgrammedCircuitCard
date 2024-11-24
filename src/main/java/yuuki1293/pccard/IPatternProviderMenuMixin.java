package yuuki1293.pccard;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Unique;

public interface IPatternProviderMenuMixin {
    @Unique
    void programmedCircuitCard$setupUpgrades();

    @Unique
    boolean programmedCircuitCard$getPCMode();

    @Unique
    IUpgradeableObject programmedCircuitCard$getHost();

    @Unique
    IUpgradeInventory programmedCircuitCard$getUpgrades();

    @Unique
    boolean programmedCircuitCard$hasUpgrade(ItemLike upgradeCard);
}
