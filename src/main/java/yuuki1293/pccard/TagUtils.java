package yuuki1293.pccard;

import com.gregtechceu.gtceu.common.data.GTItems;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class TagUtils {
    private static final String circuitResourceId =
            GTItems.PROGRAMMED_CIRCUIT.getId().toString();

    /**
     * get inputs itemStacks from Pattern. if failed, {@link Optional#empty()}<br>
     * example:<br>
     * stack.getTag() {@code {in:[{"#":8L,"#c":"ae2:i",id:"minecraft:oak_planks"},{"#":1L,"#c":"ae2:i",id:"gtceu:programmed_circuit",tag:{Configuration:8}}],out:[{"#":1L,"#c":"ae2:i",id:"minecraft:chest"}]}} <br>
     * return {@code [{"#":8L,"#c":"ae2:i",id:"minecraft:oak_planks"},{"#":1L,"#c":"ae2:i",id:"gtceu:programmed_circuit",tag:{Configuration:8}}]}
     */
    public static Optional<ListTag> getInputsFromPattern(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains("in", Tag.TAG_LIST)) {
            var inList = tag.getList("in", Tag.TAG_COMPOUND);
            return Optional.of(inList);
        }
        return Optional.empty();
    }

    /**
     * get circuit number from listTag. if failed, {@link Optional#empty()}<br>
     * example:<br>
     * listTag {@code [{"#":8L,"#c":"ae2:i",id:"minecraft:oak_planks"},{"#":1L,"#c":"ae2:i",id:"gtceu:programmed_circuit",tag:{Configuration:8}}]}
     * return 8
     * @param listTag is {@link CompoundTag} list
     * @return circuitNumber
     */
    public static Optional<Integer> getCircuitNumber(ListTag listTag) {
        for (Tag tag : listTag) {
            if (tag instanceof CompoundTag compound
                    && compound.contains("id")
                    && compound.getString("id").equals(circuitResourceId)) {
                return getCircuitNumber(compound);
            }
        }
        return Optional.empty();
    }

    /**
     * get circuit number from itemTag. if failed, {@link Optional#empty()}<br>
     * example:<br>
     * itemTag {@code {"#":1L,"#c":"ae2:i",id:"gtceu:programmed_circuit",tag:{Configuration:8}}}<br>
     * return 8
     * @param itemTag must be {@code gtceu:programmed_circuit}
     * @return circuit number
     */
    public static Optional<Integer> getCircuitNumber(CompoundTag itemTag) {
        if (itemTag.contains("tag", Tag.TAG_COMPOUND)) {
            CompoundTag innerTag = itemTag.getCompound("tag");
            if (innerTag.contains("Configuration", Tag.TAG_INT)) {
                var number = innerTag.getInt("Configuration");
                return Optional.of(number);
            }
        }
        return Optional.empty();
    }

    /**
     * remove circuit in listTag
     * @return {@code true} if remove success
     */
    public static boolean removeCircuit(ListTag listTag) {
        if (listTag == null) return false;

        return listTag.removeIf(x -> x instanceof CompoundTag compound
                && compound.contains("id")
                && compound.getString("id").equals(circuitResourceId));
    }
}
