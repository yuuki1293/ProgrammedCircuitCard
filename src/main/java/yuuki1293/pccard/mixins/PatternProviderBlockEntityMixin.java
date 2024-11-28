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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.MachineTypeHolder;

@Mixin(value = PatternProviderBlockEntity.class, remap = false)
public abstract class PatternProviderBlockEntityMixin extends AEBaseBlockEntity implements PatternProviderLogicHost, IUpgradeableObject {
    @Shadow @Final protected PatternProviderLogic logic;

    public PatternProviderBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Inject(method = "createLogic", at = @At("HEAD"))
    private void createLogic(CallbackInfoReturnable<PatternProviderLogic> cir) {
        MachineTypeHolder.MACHINE_TYPE = getItemFromBlockEntity();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) getLogic()).getUpgrades();
    }

    @Override
    public void saveChanges(){
        super.saveChanges();
        this.logic.updatePatterns();
    }
}
