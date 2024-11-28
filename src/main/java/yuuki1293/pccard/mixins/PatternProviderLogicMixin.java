package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.MachineUpgradesChanged;
import appeng.api.upgrades.UpgradeInventories;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
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

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 2000)
public abstract class PatternProviderLogicMixin implements IUpgradeableObject {
    @Unique
    private static Logger pCCard$LOGGER = LogUtils.getLogger();

    @Shadow
    public abstract void updatePatterns();

    @Shadow
    @Final
    private PatternProviderLogicHost host;
    @Unique
    private IUpgradeInventory pCCard$upgrades;
    @Unique
    private IUpgradeInventory pCCard$cache = null;

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
        if (pCCard$cache != null) return pCCard$cache;

        var upgrades = Arrays.stream(PatternProviderLogic.class.getDeclaredFields())
            .filter(f -> IUpgradeInventory.class.isAssignableFrom(f.getType()))
            .filter(f -> !f.getName().equals("pCCard$cache"))
            .map(f -> {
                f.setAccessible(true);
                try {
                    return (IUpgradeInventory) f.get(this);
                } catch (IllegalAccessException e) {
                    pCCard$LOGGER.error("Can't get field", e);
                    return null;
                }
            }).toList();

        if (upgrades.contains(null)) return pCCard$upgrades;

        try {
            var maxStackField = AppEngInternalInventory.class.getDeclaredField("maxStack");
            maxStackField.setAccessible(true);
            var count = upgrades.stream()
                .map(i -> {
                    try {
                        return ((int[]) maxStackField.get(i)).length;
                    } catch (IllegalAccessException e) {
                        pCCard$LOGGER.error("Can't get field", e);
                        return 0;
                    }
                })
                .reduce(Math::addExact).orElse(1);

            var changesCallback = pCCard$getMachineUpgradesChanged(upgrades);

            this.pCCard$cache = UpgradeInventories.forMachine(MachineTypeHolder.MACHINE_TYPE, count, changesCallback);
            return pCCard$cache;
        } catch (NoSuchFieldException e) {
            pCCard$LOGGER.error("Can't get upgrades slot", e);
            return pCCard$upgrades;
        }
    }

    @Unique
    private @NotNull MachineUpgradesChanged pCCard$getMachineUpgradesChanged(List<IUpgradeInventory> upgrades) throws NoSuchFieldException {
        var changeCallbackField = pCCard$upgrades.getClass().getDeclaredField("changeCallback");
        var callbacks = upgrades.stream()
            .map(i -> {
                try {
                    return (MachineUpgradesChanged) changeCallbackField.get(i);
                } catch (IllegalAccessException e) {
                    pCCard$LOGGER.error("Can't get field", e);
                    return null;
                }
            }).toList();
        return () -> callbacks.forEach(MachineUpgradesChanged::onUpgradesChanged);
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
        if (pCCard$hasPCCard()) {
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
                        var recipeStack = Arrays.stream(originalInputs)
                            .filter(Objects::nonNull)
                            .filter(x -> x.getPossibleInputs()[0].what().getId().equals(GTItems.INTEGRATED_CIRCUIT.getId()))
                            .findFirst()
                            .map(x -> x.getPossibleInputs()[0].what().wrapForDisplayOrFilter())
                            .orElse(ItemStack.EMPTY);
                        var number = IntCircuitBehaviour.getCircuitConfiguration(recipeStack);

                        ret.set(i, new AEPatternWrapper(definition, inputs, originalOutputs, number));
                    }
                }
            }
        }
    }

    @Unique
    private Direction pCCard$direction;

    @ModifyVariable(method = "pushPattern", at = @At(value = "STORE"), ordinal = 0,
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lappeng/helpers/patternprovider/PatternProviderLogic;rearrangeRoundRobin(Ljava/util/List;)V"),
            to = @At(value = "INVOKE", target = "Lappeng/helpers/patternprovider/PatternProviderLogic;isBlocking()Z")
        ))
    private Direction captureAdapter(Direction direction) {
        this.pCCard$direction = direction;
        return direction;
    }

    @Inject(method = "pushPattern", at = @At(value = "INVOKE", target = "Lappeng/helpers/patternprovider/PatternProviderLogic;onPushPatternSuccess(Lappeng/api/crafting/IPatternDetails;)V", ordinal = 1))
    private void onPushPatternSuccess(IPatternDetails patternDetails, KeyCounter[]
        inputHolder, CallbackInfoReturnable<Boolean> cir) {
        if (pCCard$hasPCCard() && patternDetails instanceof AEPatternWrapper pattern) {
            var be = this.host.getBlockEntity();
            var level = be.getLevel();
            if (level == null) return;

            var blockPos = be.getBlockPos().relative(pCCard$direction);
            var gtMachine = SimpleTieredMachine.getMachine(level, blockPos);
            if (gtMachine == null) return; // filter gtMachine

            if (gtMachine instanceof SimpleTieredMachine tieredMachine) {
                var inv = tieredMachine.getCircuitInventory();
                var machineStack = GTItems.INTEGRATED_CIRCUIT.asStack();

                var number = pattern.getNumber();
                IntCircuitBehaviour.setCircuitConfiguration(machineStack, number);
                inv.setStackInSlot(0, machineStack);
            }
        }
    }

    @Unique
    private boolean pCCard$hasPCCard() {
        return pCCard$upgrades.isInstalled(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get());
    }
}

