package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.CompetitionFixer;
import yuuki1293.pccard.IPatternProviderLogicMixin;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.wrapper.AEPatternWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 800)
public abstract class PatternProviderLogicMixin implements IUpgradeableObject, IPatternProviderLogicMixin {
    @Shadow
    public abstract void updatePatterns();

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow private Direction sendDirection;
    @Unique
    private IUpgradeInventory pCCard$upgrades;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        pCCard$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::pCCard$upgradesChange);
    }

    @Unique
    private void pCCard$upgradesChange() {
        this.host.saveChanges();
        updatePatterns();
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeToNBT(CompoundTag tag, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        this.pCCard$upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readFromNBT(CompoundTag tag, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        this.pCCard$upgrades.readFromNBT(tag, "upgrades");
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return pCCard$upgrades;
    }

    @Inject(method = "addDrops", at = @At("HEAD"))
    private void addDrops(List<ItemStack> drops, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        for (var is : this.pCCard$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    private void clearContent(CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) return;

        this.pCCard$upgrades.clear();
    }

    @ModifyVariable(method = "updatePatterns", at = @At("STORE"), ordinal = 0)
    private IPatternDetails updatePatterns(IPatternDetails detail) {
        if (pCCard$hasPCCard()) {
            if (detail instanceof AEProcessingPattern) {
                final var definition = detail.getDefinition();
                final var originalInputs = detail.getInputs();
                final var originalOutputs = detail.getOutputs();

                var inputs = Arrays.stream(originalInputs)
                    .filter(Objects::nonNull)
                    .filter(x -> !x.getPossibleInputs()[0].what().getId().equals(GTItems.INTEGRATED_CIRCUIT.getId())) // Check item
                    .toArray(IPatternDetails.IInput[]::new);

                if (!Arrays.equals(inputs, originalInputs)) {
                    var recipeStack = Arrays.stream(originalInputs)
                        .filter(Objects::nonNull)
                        .filter(x -> x.getPossibleInputs()[0].what().getId().equals(GTItems.INTEGRATED_CIRCUIT.getId()))
                        .findFirst()
                        .map(x -> x.getPossibleInputs()[0].what().wrapForDisplayOrFilter())
                        .orElse(ItemStack.EMPTY);
                    var number = IntCircuitBehaviour.getCircuitConfiguration(recipeStack);

                    return new AEPatternWrapper(definition, inputs, originalOutputs, number);
                }
            }
        }

        return detail;
    }

    /**
     *  NOTE: call after {@code pushPattern}
     */
    @Override
    @Unique
    public void pCCard$setPCNumber(IPatternDetails patternDetails) {
        if (pCCard$hasPCCard() && patternDetails instanceof AEPatternWrapper patternDetailsW) {
            var be = this.host.getBlockEntity();
            var level = be.getLevel();
            if (level == null) return;

            var blockPos = be.getBlockPos().relative(sendDirection);
            var gtMachine = SimpleTieredMachine.getMachine(level, blockPos);
            if (gtMachine == null) return; // filter gtMachine

            if (gtMachine instanceof SimpleTieredMachine tieredMachine) {
                var inv = tieredMachine.getCircuitInventory();
                var machineStack = GTItems.INTEGRATED_CIRCUIT.asStack();

                var number = patternDetailsW.getNumber();
                IntCircuitBehaviour.setCircuitConfiguration(machineStack, number);
                inv.setStackInSlot(0, machineStack);
            }
        }
    }

    @Unique
    public boolean pCCard$hasPCCard() {
        return isUpgradedWith(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get());
    }
}

