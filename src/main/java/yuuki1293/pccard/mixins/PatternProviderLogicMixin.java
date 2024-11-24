package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderTarget;
import com.gregtechceu.gtceu.common.data.GTItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yuuki1293.pccard.MachineTypeHolder;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.wrapper.AEPatternWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin implements IUpgradeableObject {
    @Shadow
    public abstract void updatePatterns();

    @Unique
    private IUpgradeInventory pCCard$upgrades;

    @Unique
    private PatternProviderLogicHost pCCard$host;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, PatternProviderLogicHost host, CallbackInfo ci) {
        pCCard$upgrades = UpgradeInventories.forMachine(MachineTypeHolder.MACHINE_TYPE, 1, this::pCCard$onUpgradesChanged);
        this.pCCard$host = host;
    }

    @Unique
    private void pCCard$onUpgradesChanged() {
        this.pCCard$host.saveChanges();
        this.updatePatterns();
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeToNBT(CompoundTag tag, CallbackInfo ci) {
        this.pCCard$upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readFromNBT(CompoundTag tag, CallbackInfo ci) {
        this.pCCard$upgrades.readFromNBT(tag, "upgrades");
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return pCCard$upgrades;
    }

    @Inject(method = "addDrops", at = @At("HEAD"))
    private void addDrops(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.pCCard$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    private void clearContent(CallbackInfo ci) {
        this.pCCard$upgrades.clear();
    }

    @Inject(method = "getAvailablePatterns", at = @At("RETURN"))
    private void getAvailablePatterns(CallbackInfoReturnable<List<IPatternDetails>> cir) {
        if (pCCard$upgrades.isInstalled(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) {
            var ret = cir.getReturnValue();
            for (int i = 0; i < ret.size(); i++) {
                var pattern = ret.get(i);

                if (pattern instanceof AEProcessingPattern) {
                    final var definition = pattern.getDefinition();
                    final var originalInputs = pattern.getInputs();
                    final var originalOutputs = pattern.getOutputs();

                    var inputs = Arrays.stream(originalInputs)
                        .filter(Objects::nonNull)
                        .filter(x -> !x.getPossibleInputs()[0].what().getId().equals(GTItems.INTEGRATED_CIRCUIT.getId())) // Check item
                        .toArray(IPatternDetails.IInput[]::new);

                    if (!Arrays.equals(inputs, originalInputs)) {
                        ret.set(i, new AEPatternWrapper(definition, inputs, originalOutputs));
                    }
                }
            }
        }
    }

    @Unique
    private PatternProviderTarget target;

    @ModifyVariable(method = "pushPattern", at = @At(value = "STORE"), ordinal = 0,
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lappeng/helpers/patternprovider/PatternProviderLogic;rearrangeRoundRobin(Ljava/util/List;)V"),
            to = @At(value = "INVOKE", target = "Lappeng/helpers/patternprovider/PatternProviderLogic;isBlocking()Z")
        ))
    private PatternProviderTarget captureAdapter(PatternProviderTarget adapter) {
        this.target = adapter;
        return adapter;
    }

    @Inject(method = "onPushPatternSuccess", at = @At("HEAD"))
    private void onPushPatternSuccess(IPatternDetails pattern, CallbackInfo ci) {
        target
    }
}

