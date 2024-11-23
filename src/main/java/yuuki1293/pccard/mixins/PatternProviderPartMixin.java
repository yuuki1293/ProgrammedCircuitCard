package yuuki1293.pccard.mixins;

import appeng.api.parts.IPartItem;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.MachineTypeHolder;

@Mixin(value = PatternProviderPart.class, remap = false)
public abstract class PatternProviderPartMixin extends AEBasePart implements IUpgradeableObject {
    public PatternProviderPartMixin(IPartItem<?> partItem) {
        super(partItem);
    }

    @Inject(method = "createLogic", at = @At("HEAD"))
    private void createLogic(CallbackInfoReturnable<PatternProviderLogic> cir) {
        MachineTypeHolder.MACHINE_TYPE = getPartItem().asItem();
    }
}
