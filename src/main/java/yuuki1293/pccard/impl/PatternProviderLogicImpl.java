package yuuki1293.pccard.impl;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.parts.storagebus.StorageBusPart;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.item.behavior.IntCircuitBehaviour;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;
import yuuki1293.pccard.TagUtils;
import yuuki1293.pccard.wrapper.IAEPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PatternProviderLogicImpl {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ItemStack updatePatterns(IPatternProviderLogicMixin self, ItemStack stack) {
        if (self.pCCard$hasPCCard()) {
            var newStack = stack.copy();
            var number = TagUtils.extractCircuitNumber(newStack);
            if (number >= 0) {
                newStack.set(PCCard.RECIPE_CIRCUIT, number);
            }

            return newStack;
        }

        return stack;
    }

    public static void setPCNumber(IPatternProviderLogicMixin self, IPatternDetails patternDetails) {
        try {
            if (self.pCCard$hasPCCard() && patternDetails instanceof IAEPattern patternDetailsW) {
                var be = self.pCCard$getBlockEntity();
                var level = be.getLevel();
                if (level == null) return;

                var blockPoses = self.pCCard$getSendPos();

                for (var blockPos : blockPoses) {
                    var gtMachine = SimpleTieredMachine.getMachine(level, blockPos);
                    if (gtMachine == null) return; // filter gtMachine

                    if (gtMachine instanceof IHasCircuitSlot machine) {
                        var inv = machine.getCircuitInventory();
                        setInvNumber(inv, patternDetailsW);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to set PC number", e);
        }
    }

    private static void setInvNumber(NotifiableItemStackHandler inv, IAEPattern details) throws IndexOutOfBoundsException {
        var machineStack = GTItems.PROGRAMMED_CIRCUIT.asStack();

        var number = details.pCCard$getNumber();
        IntCircuitBehaviour.setCircuitConfiguration(machineStack, number);
        inv.setStackInSlot(0, machineStack);
    }

    /**
     * get BlockPos which ingredient are sent. include subnet.
     * @param self caller
     * @param parent Class of mixin target
     * @return all send posses
     */
    public static List<BlockPos> getSendPos(Level level, IPatternProviderLogicMixin self, Class<?> parent) {
        var posDir = getSendPosDirect(self, parent);
        var pos2 = getSendPosSubnet(level, posDir.getA(), posDir.getB().getOpposite());

        if (pos2.isEmpty()) {
            return List.of(posDir.getA());
        } else {
            return pos2;
        }
    }

    /**
     * support MAE2 pattern p2p
     */
    public static Tuple<BlockPos, Direction> getSendPosDirect(IPatternProviderLogicMixin self, Class<?> parent) {
        try {
            var dir = self.pCCard$getSendDirection();

            // stone.mae2.mixins.PatternProviderLogicMixin.pushPattern
            if (Arrays.stream(parent.getDeclaredFields()).anyMatch(f -> f.getName().equals("sendPos"))) {
                var posFiled = parent.getDeclaredField("sendPos");
                posFiled.setAccessible(true);
                var pos = posFiled.get(self);

                if (pos != null)
                    return new Tuple<>((BlockPos) posFiled.get(self), dir);
            }

            var be = self.pCCard$getBlockEntity();

            return new Tuple<>(be.getBlockPos().relative(dir), dir);
        } catch (Exception e) {
            LOGGER.error("Error while getting sendPos", e);
            return new Tuple<>(BlockPos.ZERO, Direction.UP);
        }
    }

    /**
     * get BlockPos which ingredient are sent in subnet.
     * @param level level
     * @param pos interface pos
     * @param side interface side
     * @return storage bus dest
     */
    public static List<BlockPos> getSendPosSubnet(Level level, BlockPos pos, Direction side) {
        var host = getActionHost(level, pos, side);
        var grid = getGrid(host);
        var parts = getStorageBusParts(grid);
        return getBlockPoses(parts);
    }

    /**
     * get action host from blockEntity or part
     */
    private static IActionHost getActionHost(Level level, BlockPos pos, Direction side) {
        var be = level.getBlockEntity(pos);

        if (be instanceof IActionHost host) return host;

        if (be instanceof IPartHost partHost) {
            var part = partHost.getPart(side);
            if (part instanceof IActionHost host) return host;
        }

        return null;
    }

    /**
     * get Grid
     */
    private static IGrid getGrid(IActionHost host) {
        if (host == null) return null;

        var node = host.getActionableNode();
        if (node != null) {
            return node.getGrid();
        }
        return null;
    }

    /**
     * get all StorageBusPart in grid
     */
    private static Set<StorageBusPart> getStorageBusParts(IGrid grid) {
        if (grid == null) {
            return Set.of();
        }

        return grid.getMachines(StorageBusPart.class);
    }

    /**
     * get BlockPos es from storageBusPart list
     */
    private static List<BlockPos> getBlockPoses(Iterable<StorageBusPart> parts) {
        var poses = new ArrayList<BlockPos>();

        for (var part : parts) {
            var pos = part.getBlockEntity().getBlockPos();
            var side = part.getSide();
            var machinePos = pos.relative(side);
            poses.add(machinePos);
        }

        return poses;
    }
}
