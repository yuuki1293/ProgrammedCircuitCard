package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PatternProviderBlockEntity.class, remap = false)
public abstract class PatternProviderBlockEntityMixin extends AEBaseBlockEntity implements PatternProviderLogicHost, IUpgradeableObject {
    @Shadow @Final protected PatternProviderLogic logic;

    public PatternProviderBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) getLogic()).getUpgrades();
    }
}
