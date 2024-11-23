package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PatternProviderBlockEntity.class)
public abstract class PatternProviderBlockEntityMixin implements IUpgradeableObject {
}
