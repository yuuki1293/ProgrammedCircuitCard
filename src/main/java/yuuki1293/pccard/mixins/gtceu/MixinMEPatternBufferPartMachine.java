package yuuki1293.pccard.mixins.gtceu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfiguratorButton;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancySelectorConfigurator;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.impl.PatternBufferBlockingMode;
import yuuki1293.pccard.impl.PatternBufferBlockingPolicy;
import yuuki1293.pccard.impl.PatternBufferCardConfigurator;
import yuuki1293.pccard.impl.PatternBufferCardInventory;
import yuuki1293.pccard.impl.PatternProviderLogicImpl;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public abstract class MixinMEPatternBufferPartMachine extends MEBusPartMachine {

    @Unique
    private static final int PCCARD$CIRCUIT_IDLE = -1;
    @Unique
    private static final int PCCARD$CIRCUIT_UNKNOWN = -2;

    @Unique
    @Persisted(key = "pccardCardInventory")
    @DescSynced
    private CustomItemStackHandler pCCard$cardInventory;

    @Unique
    @Persisted(key = "pccardBlockingEnabled")
    @DescSynced
    private boolean pCCard$blockingEnabled;

    @Unique
    @Persisted(key = "pccardBlockingMode")
    @DescSynced
    private PatternBufferBlockingMode pCCard$blockingMode;

    @Unique
    @Persisted(key = "pccardActiveCircuit")
    private int pCCard$activeCircuit;

    @Unique
    private boolean pCCard$removing;
    @Unique
    private boolean pCCard$lastTransformationEnabled;

    protected MixinMEPatternBufferPartMachine(IMachineBlockEntity holder, IO io, Object... args) {
        super(holder, io, args);
    }

    @Inject(
        method = "<init>(Lcom/gregtechceu/gtceu/api/machine/IMachineBlockEntity;[Ljava/lang/Object;)V",
        at = @At("TAIL"))
    private void pCCard$initialize(IMachineBlockEntity holder, Object[] args, CallbackInfo ci) {
        pCCard$cardInventory = new PatternBufferCardInventory(
            this::pCCard$canChangePatternBufferCard,
            this::pCCard$onPatternBufferCardChanged);
        pCCard$blockingMode = PatternBufferBlockingMode.NORMAL;
        pCCard$activeCircuit = PCCARD$CIRCUIT_IDLE;
        ((MEPatternBufferPartMachine) (Object) this).getCircuitInventory()
            .addChangedListener(this::pCCard$enforceCircuitLease);
    }

    @ModifyArg(
        method = "onPatternChange",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/crafting/PatternDetailsHelper;decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;"),
        index = 0)
    private ItemStack pCCard$updateChangedPattern(ItemStack stack) {
        return pCCard$canTransformPatterns() ? PatternProviderLogicImpl.updatePatterns(stack) : stack;
    }

    @ModifyArg(
        method = "lambda$onLoad$1",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/crafting/PatternDetailsHelper;decodePattern(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lappeng/api/crafting/IPatternDetails;"),
        index = 0,
        require = 1)
    private ItemStack pCCard$updateLoadedPattern(ItemStack stack) {
        return pCCard$canTransformPatterns() ? PatternProviderLogicImpl.updatePatterns(stack) : stack;
    }

    @Inject(method = "onLoad", at = @At("TAIL"))
    private void pCCard$onLoad(CallbackInfo ci) {
        if (isRemote()) return;
        ((PatternBufferCardInventory) pCCard$cardInventory).synchronizeCardState();
        pCCard$lastTransformationEnabled = pCCard$canTransformPatterns();
        pCCard$reconcileCircuitLease();
        pCCard$enforceCircuitLease();
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void pCCard$onUpdate(CallbackInfo ci) {
        if (isRemote()) return;

        var transformationEnabled = pCCard$canTransformPatterns();
        if (transformationEnabled != pCCard$lastTransformationEnabled) {
            pCCard$lastTransformationEnabled = transformationEnabled;
            pCCard$refreshPatterns();
        }

        pCCard$reconcileCircuitLease();
        pCCard$enforceCircuitLease();
    }

    @Inject(method = "attachConfigurators", at = @At("TAIL"))
    private void pCCard$attachConfigurators(ConfiguratorPanel configuratorPanel, CallbackInfo ci) {
        var blockingConfigurator = new IFancyConfiguratorButton.Toggle(
            new GuiTextureGroup(GuiTextures.BUTTON, PatternBufferBlockingMode.SMART.getIcon()),
            new GuiTextureGroup(GuiTextures.BUTTON, PatternBufferBlockingMode.NORMAL.getIcon()),
            () -> pCCard$blockingEnabled,
            (clickData, enabled) -> pCCard$setBlockingEnabled(enabled)).setTooltipsSupplier(
                enabled -> List.of(
                    Component.translatable(
                        enabled ? "gui.pccard.pattern_buffer.blocking.enabled"
                            : "gui.pccard.pattern_buffer.blocking.disabled"),
                    Component.translatable(
                        enabled ? "gui.pccard.pattern_buffer.blocking.enabled.description"
                            : "gui.pccard.pattern_buffer.blocking.disabled.description")));

        var modeConfigurator = new FancySelectorConfigurator<>(
            PatternBufferBlockingMode.VALUES,
            pCCard$getBlockingMode(),
            this::pCCard$setBlockingMode).setTooltip(
                mode -> List.of(
                    Component.translatable(
                        "gui.pccard.pattern_buffer.blocking_mode",
                        Component.translatable(mode.translationKey())),
                    Component.translatable(mode.descriptionKey())));

        configuratorPanel.attachConfigurators(blockingConfigurator, modeConfigurator);
    }

    @Inject(method = "createUIWidget", at = @At("RETURN"), require = 1)
    private void pCCard$attachUpgradePanel(CallbackInfoReturnable<Widget> cir) {
        if (cir.getReturnValue() instanceof WidgetGroup root) {
            PatternBufferCardConfigurator.attachUpgradePanel(root, pCCard$cardInventory);
        }
    }

    @Inject(
        method = "pushPattern",
        at = @At(
            value = "INVOKE",
            target = "Lcom/gregtechceu/gtceu/integration/ae2/machine/MEPatternBufferPartMachine$InternalSlot;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)V"),
        cancellable = true)
    private void pCCard$preflightPush(IPatternDetails patternDetails, KeyCounter[] inputHolder,
        CallbackInfoReturnable<Boolean> cir) {
        var bufferedKeys = pCCard$getBufferedKeys();
        if (pCCard$blockingEnabled && pCCard$isBlocked(patternDetails, bufferedKeys)) {
            cir.setReturnValue(false);
            return;
        }

        var circuitNumber = PatternProviderLogicImpl.getCircuitNumber(patternDetails);
        if (circuitNumber.isEmpty()) return;

        if (!pCCard$canTransformPatterns() || !pCCard$hasIncomingPayload(inputHolder)) {
            cir.setReturnValue(false);
            return;
        }

        var wantedCircuit = circuitNumber.get();
        if (PatternBufferBlockingPolicy.circuitConflict(!bufferedKeys.isEmpty(), pCCard$activeCircuit, wantedCircuit)) {
            cir.setReturnValue(false);
            return;
        }

        if (bufferedKeys.isEmpty()) {
            pCCard$setActiveCircuit(wantedCircuit);
        }

        var patternBuffer = (MEPatternBufferPartMachine) (Object) this;
        PatternProviderLogicImpl.setPCNumber(patternBuffer.getCircuitInventory(), wantedCircuit);
    }

    @Inject(method = "onMachineRemoved", at = @At("HEAD"))
    private void pCCard$dropCard(CallbackInfo ci) {
        pCCard$removing = true;
        if (pCCard$cardInventory != null) {
            clearInventory(pCCard$cardInventory);
        }
    }

    @Unique
    private boolean pCCard$canChangePatternBufferCard() {
        return pCCard$removing || !pCCard$hasBufferedPayload();
    }

    @Unique
    private void pCCard$onPatternBufferCardChanged() {
        if (pCCard$removing || isRemote() || getLevel() == null) return;

        pCCard$lastTransformationEnabled = pCCard$canTransformPatterns();
        pCCard$setActiveCircuit(PCCARD$CIRCUIT_IDLE);
        pCCard$refreshPatterns();
        markDirty();
    }

    @Unique
    private boolean pCCard$canTransformPatterns() {
        if (
            pCCard$cardInventory == null || pCCard$cardInventory.getStackInSlot(0)
                .isEmpty()
        ) return false;
        if (
            !pCCard$cardInventory.getStackInSlot(0)
                .is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())
        ) return false;

        var patternBuffer = (MEPatternBufferPartMachine) (Object) this;
        return isHasCircuitSlot() && isCircuitSlotEnabled()
            && patternBuffer.getCircuitInventory()
                .getSlots() > 0;
    }

    @Unique
    private void pCCard$refreshPatterns() {
        if (isRemote() || getLevel() == null) return;

        var patternInventory = ((MEPatternBufferPartMachine) (Object) this).getTerminalPatternInventory();
        for (int slot = 0; slot < patternInventory.size(); slot++) {
            patternInventory.setItemDirect(
                slot,
                patternInventory.getStackInSlot(slot)
                    .copy());
        }
    }

    @Unique
    private void pCCard$setBlockingEnabled(boolean enabled) {
        if (pCCard$blockingEnabled == enabled) return;
        pCCard$blockingEnabled = enabled;
        if (!isRemote()) markDirty();
    }

    @Unique
    private PatternBufferBlockingMode pCCard$getBlockingMode() {
        return pCCard$blockingMode == null ? PatternBufferBlockingMode.NORMAL : pCCard$blockingMode;
    }

    @Unique
    private void pCCard$setBlockingMode(PatternBufferBlockingMode mode) {
        if (mode == null || pCCard$getBlockingMode() == mode) return;
        pCCard$blockingMode = mode;
        if (!isRemote()) markDirty();
    }

    @Unique
    private boolean pCCard$isBlocked(IPatternDetails incomingPattern, Set<AEKey> bufferedKeys) {
        return switch (pCCard$getBlockingMode()) {
            case FULL -> PatternBufferBlockingPolicy.full(bufferedKeys);
            case NORMAL -> {
                var advertisedInputs = new HashSet<AEKey>();
                for (var pattern : ((MEPatternBufferPartMachine) (Object) this).getAvailablePatterns()) {
                    pCCard$addPatternInputs(pattern, advertisedInputs);
                }
                yield PatternBufferBlockingPolicy.normal(bufferedKeys, advertisedInputs);
            }
            case SMART -> {
                var incomingInputs = new HashSet<AEKey>();
                pCCard$addPatternInputs(incomingPattern, incomingInputs);
                yield PatternBufferBlockingPolicy.smart(bufferedKeys, incomingInputs);
            }
        };
    }

    @Unique
    private void pCCard$addPatternInputs(IPatternDetails pattern, Set<AEKey> inputs) {
        for (var input : pattern.getInputs()) {
            for (var candidate : input.getPossibleInputs()) {
                inputs.add(
                    candidate.what()
                        .dropSecondary());
            }
        }
    }

    @Unique
    private Set<AEKey> pCCard$getBufferedKeys() {
        var keys = new HashSet<AEKey>();
        var internalInventory = ((MEPatternBufferPartMachine) (Object) this).getInternalInventory();
        for (var internalSlot : internalInventory) {
            for (var stack : internalSlot.getItems()) {
                var key = AEItemKey.of(stack);
                if (key != null && !PatternProviderLogicImpl.isProgrammedCircuit(key)) {
                    keys.add(key.dropSecondary());
                }
            }
            for (var stack : internalSlot.getFluids()) {
                var key = AEFluidKey.of(stack);
                if (key != null) keys.add(key.dropSecondary());
            }
        }
        return keys;
    }

    @Unique
    private boolean pCCard$hasBufferedPayload() {
        var internalInventory = ((MEPatternBufferPartMachine) (Object) this).getInternalInventory();
        for (var internalSlot : internalInventory) {
            if (!internalSlot.isFluidEmpty()) return true;
            for (var stack : internalSlot.getItems()) {
                var key = AEItemKey.of(stack);
                if (key != null && !PatternProviderLogicImpl.isProgrammedCircuit(key)) return true;
            }
        }
        return false;
    }

    @Unique
    private boolean pCCard$hasIncomingPayload(KeyCounter[] inputHolder) {
        for (var inputs : inputHolder) {
            for (var input : inputs) {
                if (input.getLongValue() > 0 && !PatternProviderLogicImpl.isProgrammedCircuit(input.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private void pCCard$reconcileCircuitLease() {
        var hasPayload = pCCard$hasBufferedPayload();
        if (!hasPayload && pCCard$activeCircuit != PCCARD$CIRCUIT_IDLE) {
            pCCard$setActiveCircuit(PCCARD$CIRCUIT_IDLE);
        } else if (hasPayload && pCCard$activeCircuit == PCCARD$CIRCUIT_IDLE && pCCard$canTransformPatterns()) {
            pCCard$setActiveCircuit(PCCARD$CIRCUIT_UNKNOWN);
        }
    }

    @Unique
    private void pCCard$enforceCircuitLease() {
        if (pCCard$activeCircuit < 0 || !pCCard$hasBufferedPayload()) return;

        var patternBuffer = (MEPatternBufferPartMachine) (Object) this;
        var circuitInventory = patternBuffer.getCircuitInventory();
        if (circuitInventory.getSlots() == 0) return;

        var current = circuitInventory.getStackInSlot(0);
        if (
            !IntCircuitBehaviour.isIntegratedCircuit(current)
                || IntCircuitBehaviour.getCircuitConfiguration(current) != pCCard$activeCircuit
        ) {
            PatternProviderLogicImpl.setPCNumber(circuitInventory, pCCard$activeCircuit);
        }
    }

    @Unique
    private void pCCard$setActiveCircuit(int circuit) {
        if (pCCard$activeCircuit == circuit) return;
        pCCard$activeCircuit = circuit;
        if (!isRemote()) markDirty();
    }
}
