package yuuki1293.pccard.wrapper;

import net.minecraft.world.item.ItemStack;

import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;

public interface IPatternBufferPCC {

    boolean pCCard$canChangePatternBufferCard();

    void pCCard$onPatternBufferCardChanged();

    ItemStack pCCard$transformPatternBufferPattern(ItemStack stack);

    boolean pCCard$preflightPatternBufferPush(IPatternDetails patternDetails, KeyCounter[] inputHolder);

    void pCCard$attachPatternBufferConfigurators(ConfiguratorPanel configuratorPanel);
}
