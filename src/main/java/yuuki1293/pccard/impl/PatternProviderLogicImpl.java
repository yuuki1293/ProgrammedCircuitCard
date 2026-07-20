package yuuki1293.pccard.impl;

import static yuuki1293.pccard.NBTs.NBT_CIRCUIT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.slf4j.Logger;

import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.IHasCircuitSlot;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.mojang.logging.LogUtils;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.api.stacks.AEItemKey;
import appeng.parts.storagebus.StorageBusPart;
import yuuki1293.pccard.ConfigCommon;
import yuuki1293.pccard.TagUtils;
import yuuki1293.pccard.wrapper.IAEPattern;
import yuuki1293.pccard.wrapper.IPatternP2PTunnelLogicMixin;

public class PatternProviderLogicImpl {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ItemStack updatePatterns(ItemStack stack) {
        var newStack = stack.copy();
        var inputs = TagUtils.getInputsFromPattern(newStack);
        var tagRoot = newStack.getTag();
        if (tagRoot == null) { // if null, create new empty tag
            tagRoot = new CompoundTag();
        }

        if (inputs.isPresent()) {
            var number = TagUtils.getCircuitNumber(inputs.get())
                .orElse(0);
            tagRoot.putInt(NBT_CIRCUIT, number);
            TagUtils.removeCircuit(inputs.get());
        }

        return newStack;
    }

    public static void addCircuitToPatternInputs(IPatternDetails patternDetails, Set<appeng.api.stacks.AEKey> inputs) {
        var definitionTag = patternDetails.getDefinition()
            .getTag();
        if (definitionTag != null && definitionTag.contains(NBT_CIRCUIT)) {
            inputs.add(AEItemKey.of(IntCircuitBehaviour.stack(definitionTag.getInt(NBT_CIRCUIT))));
        }
    }

    public static void setPCNumber(IPatternDetails patternDetails, BlockEntity be, List<BlockPos> blockPoses) {
        try {
            if (patternDetails instanceof IAEPattern patternDetailsW) {
                var level = be.getLevel();
                if (level == null) return;

                for (var blockPos : blockPoses) {
                    var gtMachine = SimpleTieredMachine.getMachine(level, blockPos);
                    if (gtMachine == null) continue; // filter gtMachine

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

    private static void setInvNumber(NotifiableItemStackHandler inv, IAEPattern details)
        throws IndexOutOfBoundsException {
        var machineStack = GTItems.PROGRAMMED_CIRCUIT.asStack();

        var number = details.pCCard$getNumber();
        IntCircuitBehaviour.setCircuitConfiguration(machineStack, number);
        inv.setStackInSlot(0, machineStack);
    }

    /**
     * get BlockPos which ingredient are sent. include subnet.
     * Uses breadth-first search to traverse tree up to configured depth.
     * 
     * @param be        Pattern Provider
     * @param direction Item push direction
     * @return all leaf nodes within configured depth
     */
    public static List<BlockPos> getSendPos(BlockEntity be, Direction direction) {
        var level = be.getLevel();
        if (level == null) return List.of();

        var rootPosDir = getSendPosDirect(level, be, direction);
        var allLeafNodes = new ArrayList<BlockPos>();
        var visited = new HashSet<Tuple<BlockPos, Direction>>();
        var queue = new LinkedList<Tuple<Tuple<BlockPos, Direction>, Integer>>();

        // Start with root node at depth 0
        queue.offer(new Tuple<>(rootPosDir, 0));

        while (!queue.isEmpty()) {
            var current = queue.poll();
            var posDir = current.getA();
            int depth = current.getB();

            // Skip if already visited or if depth exceeds configured limit
            if (visited.contains(posDir) || depth > ConfigCommon.getSearchDepth()) {
                continue;
            }

            visited.add(posDir);

            // Get children nodes from subnet
            var children = getSendPosSubnet(
                level,
                posDir.getA(),
                posDir.getB()
                    .getOpposite());

            if (children.isEmpty()) {
                // This is a leaf node, add to results
                allLeafNodes.add(posDir.getA());
            } else {
                // Add children to queue for next level traversal
                for (var childPosDir : children) {
                    queue.offer(new Tuple<>(childPosDir, depth + 1));
                }
            }
        }

        // If no leaf nodes found, return the root
        if (allLeafNodes.isEmpty()) {
            allLeafNodes.add(rootPosDir.getA());
        }

        return allLeafNodes;
    }

    /**
     * support MAE2 pattern p2p
     */
    public static Tuple<BlockPos, Direction> getSendPosDirect(@Nullable Level level, BlockEntity be,
        Direction direction) {
        try {
            if (level == null) return new Tuple<>(BlockPos.ZERO, Direction.UP);

            var adjPos = be.getBlockPos()
                .relative(direction);

            // For MAE2
            {
                var adjBe = level.getBlockEntity(adjPos);
                var adjBeSide = direction.getOpposite();
                var craftingMachine = ICraftingMachine.of(level, adjPos, adjBeSide, adjBe);
                if (craftingMachine instanceof IPatternP2PTunnelLogicMixin patternP2P) {
                    var patternP2PPos = patternP2P.pCCard$getLastBlockPos();
                    var patternP2PDirection = patternP2P.pCCard$getLastDirection();
                    return new Tuple<>(patternP2PPos, patternP2PDirection);
                }
            }

            return new Tuple<>(adjPos, direction);
        } catch (Exception e) {
            LOGGER.error("Error while getting sendPos", e);
            return new Tuple<>(BlockPos.ZERO, Direction.UP);
        }
    }

    /**
     * get BlockPos which ingredient are sent in subnet.
     * 
     * @param level level
     * @param pos   interface pos
     * @param side  interface side
     * @return storage bus dest
     */
    public static List<Tuple<BlockPos, Direction>> getSendPosSubnet(Level level, BlockPos pos, Direction side) {
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
    private static List<Tuple<BlockPos, Direction>> getBlockPoses(Iterable<StorageBusPart> parts) {
        var poses = new ArrayList<Tuple<BlockPos, Direction>>();

        for (var part : parts) {
            var pos = part.getBlockEntity()
                .getBlockPos();
            var side = part.getSide();
            var machinePos = pos.relative(side);
            poses.add(new Tuple<>(machinePos, side.getOpposite()));
        }

        return poses;
    }
}
