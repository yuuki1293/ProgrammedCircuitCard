package yuuki1293.pccard;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Unique;

public interface IPatternProviderMenuMixin {
    @Unique
    void pCCard$setupUpgrades();

    @Unique
    boolean pCCard$getPCMode();

    @Unique
    IUpgradeableObject pCCard$getHost();

    @Unique
    IUpgradeInventory pCCard$getUpgrades();

    @Unique
    boolean pCCard$hasUpgrade(ItemLike upgradeCard);
}
