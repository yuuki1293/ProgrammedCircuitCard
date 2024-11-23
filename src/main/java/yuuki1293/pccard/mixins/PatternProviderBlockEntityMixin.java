package yuuki1293.pccard.mixins;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.MachineTypeHolder;

@Mixin(value = PatternProviderBlockEntity.class, remap = false)
public abstract class PatternProviderBlockEntityMixin extends AEBaseBlockEntity implements IUpgradeableObject {
    public PatternProviderBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "createLogic", at = @At("HEAD"))
    private void createLogic(CallbackInfoReturnable<PatternProviderLogic> cir) {
        MachineTypeHolder.MACHINE_TYPE = getItemFromBlockEntity();
    }
}
