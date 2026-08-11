package de.fene296.essentia.datagen;

import de.fene296.essentia.datagen.recipe.EssenceExtractorRecipeBuilder;
import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.recipe.essence_extractor.EssenceExtractorRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

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

        essenceExtractorRecipes();
    }


    private void essenceExtractorRecipes() {
        EssenceExtractorRecipeBuilder.extractRecipe(RecipeCategory.MISC,
                        Items.DIRT, 4,
                        EssentiaItems.EARTH_ESSENCE_CRYSTAL.get(), 1)
                .unlockedBy(getHasName(Items.DIRT), has(Items.DIRT))
                .save(output, "essentia:essence_extractor/earth_essence_from_dirt");

        EssenceExtractorRecipeBuilder.extractRecipe(RecipeCategory.MISC,
                        Items.CLAY_BALL, 2,
                        EssentiaItems.EARTH_ESSENCE_CRYSTAL.get(), 1)
                .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                .save(output, "essentia:essence_extractor/earth_essence_from_clay");

        EssenceExtractorRecipeBuilder.extractRecipe(RecipeCategory.MISC,
                        Items.DRAGON_BREATH, 1,
                        EssentiaItems.CHAOS_ESSENCE_CRYSTAL.get(), 1)
                .duration(200)
                .unlockedBy(getHasName(Items.DRAGON_BREATH), has(Items.DRAGON_BREATH))
                .save(output, "essentia:essence_extractor/chaos_essence_from_dragon_breath");

        EssenceExtractorRecipeBuilder.extractRecipe(RecipeCategory.MISC,
                Items.BLAZE_ROD, 1,
                EssentiaItems.LIGHT_ESSENCE_CRYSTAL, 1)
                .dust(Items.GLOWSTONE_DUST, 2)
                .crystal(Items.QUARTZ, 1)
                .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                .save(output, "essentia:essence_extractor/light_essence_from_blaze_rod");

    }
}
