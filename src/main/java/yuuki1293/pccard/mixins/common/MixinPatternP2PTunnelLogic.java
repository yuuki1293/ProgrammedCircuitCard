package yuuki1293.pccard.mixins.common;

import appeng.api.implementations.blockentities.ICraftingMachine;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stone.mae2.parts.p2p.PatternP2PTunnelLogic;
import yuuki1293.pccard.wrapper.IPatternP2PTunnelLogicMixin;

@Pseudo
@Mixin(value = PatternP2PTunnelLogic.class, remap = false)
public abstract class MixinPatternP2PTunnelLogic implements IPatternP2PTunnelLogicMixin {
    @Unique
    private BlockPos pCCard$lastBlockPos;

    @Unique
    private Direction pCCard$lastDirection;

    @Inject(
            method = "pushPattern",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/implementations/blockentities/ICraftingMachine;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;Lnet/minecraft/core/Direction;)Z"))
    public void pushPattern(CallbackInfoReturnable<Boolean> cir, @Local ICraftingMachine craftingMachine) {
        if (craftingMachine instanceof IPatternP2PTunnelLogicMixin patternP2PTunnelLogicMixin) {
            this.pCCard$lastBlockPos = patternP2PTunnelLogicMixin.pCCard$getLastBlockPos();
            this.pCCard$lastDirection = patternP2PTunnelLogicMixin.pCCard$getLastDirection();
        }
    }

    @Inject(
            method = "pushPattern",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/crafting/IPatternDetails;pushInputsToExternalInventory([Lappeng/api/stacks/KeyCounter;Lappeng/api/crafting/IPatternDetails$PatternInputSink;)V"))
    public void pushInputsToExternalInventory(
            CallbackInfoReturnable<Boolean> cir, @Local PatternP2PTunnelLogic.Target output) {
        this.pCCard$lastBlockPos = output.pos();
        this.pCCard$lastDirection = output.side();
    }

    @Unique
    @Override
    public BlockPos pCCard$getLastBlockPos() {
        return this.pCCard$lastBlockPos;
    }

    @Unique
    @Override
    public Direction pCCard$getLastDirection() {
        return this.pCCard$lastDirection;
    }
}
