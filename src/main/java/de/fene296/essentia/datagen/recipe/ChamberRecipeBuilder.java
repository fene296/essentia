package de.fene296.essentia.datagen.recipe;

import de.fene296.essentia.recipe.ChamberRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public class ChamberRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final ItemStackTemplate result;
    private final Ingredient ingredient;
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private @Nullable String group;

    private ChamberRecipeBuilder(RecipeCategory category, Ingredient ingredient, ItemStackTemplate result) {
        this.category = category;
        this.result = result;
        this.ingredient = ingredient;
    }

    public static ChamberRecipeBuilder crystallizerRecipe(RecipeCategory category, Ingredient ingredient, ItemLike result, int count) {
        return new ChamberRecipeBuilder(category, ingredient, new ItemStackTemplate(result.asItem(), count));
    }

    public static ChamberRecipeBuilder crystallizerRecipe(RecipeCategory category, Ingredient ingredient, ItemLike result) {
        return new ChamberRecipeBuilder(category, ingredient, new ItemStackTemplate(result.asItem()));
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        advancementBuilder.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        ChamberRecipe recipe = new ChamberRecipe(this.ingredient, this.result);
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }
}
