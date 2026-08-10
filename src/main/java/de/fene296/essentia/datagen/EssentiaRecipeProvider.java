package de.fene296.essentia.datagen;

import de.fene296.essentia.datagen.recipe.ChamberRecipeBuilder;
import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.recipe.ChamberRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class EssentiaRecipeProvider extends RecipeProvider {
    public EssentiaRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new EssentiaRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Essentia Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shapeless(RecipeCategory.MISC, EssentiaItems.ESSENCE_DUST.get(), 4)
                .requires(Items.GLOWSTONE_DUST)
                .requires(Items.REDSTONE)
                .requires(Items.AMETHYST_SHARD)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                .group("essentia")
                .save(output);


        ChamberRecipeBuilder.crystallizerRecipe(RecipeCategory.MISC,
                        Ingredient.of(Items.DIRT), 4,
                        Ingredient.of(EssentiaItems.ESSENCE_DUST.get()), 1,
                        Ingredient.of(EssentiaItems.PRIMORDIAL_CRYSTAL.get()), 1,
                        EssentiaItems.EARTH_ESSENCE_CRYSTAL.get())
                .unlockedBy(getHasName(Items.DIRT), has(Items.DIRT))
                .save(output);

        ChamberRecipeBuilder.crystallizerRecipe(RecipeCategory.MISC,
                        Ingredient.of(Items.DRAGON_BREATH), 1,
                        Ingredient.of(EssentiaItems.ESSENCE_DUST.get()), 1,
                        Ingredient.of(EssentiaItems.PRIMORDIAL_CRYSTAL.get()), 1,
                        EssentiaItems.CHAOS_ESSENCE_CRYSTAL.get())
                .unlockedBy(getHasName(Items.DRAGON_BREATH), has(Items.DRAGON_BREATH))
                .save(output);
    }
}
