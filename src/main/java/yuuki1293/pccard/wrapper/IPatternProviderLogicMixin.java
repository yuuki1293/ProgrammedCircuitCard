package yuuki1293.pccard.wrapper;

import appeng.api.crafting.IPatternDetails;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPatternProviderLogicMixin {
    /**
     *  NOTE: call after {@code pushPattern}
     */
    void pCCard$setPCNumber(IPatternDetails patternDetails);

    boolean pCCard$hasPCCard();

    /**
     * @return machine block pos
     */
    List<BlockPos> pCCard$getSendPos();

    /**
     * @return send direction
     */
    Direction pCCard$getSendDirection();

    /**
     * set send direction
     */
    void pCCard$setSendDirection(Direction direction);

    /**
     * @return host's BlockEntity
     */
    BlockEntity pCCard$getBlockEntity();

    /**
     * @return the level where the host BlockEntity is located
     */
    Level pCCard$getLevel();
}
