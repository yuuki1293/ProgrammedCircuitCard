package yuuki1293.pccard.datagen;

import appeng.core.definitions.AEItems;
import com.gregtechceu.gtceu.data.item.GTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import org.jetbrains.annotations.NotNull;
import yuuki1293.pccard.PCCard;

import java.util.concurrent.CompletableFuture;

public class PCCRecipeProvider extends RecipeProvider {
    static String C = "has_item";

    public PCCRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput c){
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM)
            .requires(AEItems.CRAFTING_CARD)
            .requires(GTItems.PROGRAMMED_CIRCUIT)
            .unlockedBy(C, has(GTItems.PROGRAMMED_CIRCUIT))
            .save(c, PCCard.id("card_programmed_circuit"));
    }
}
