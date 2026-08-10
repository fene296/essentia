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

    private final Ingredient resource;
    private final int resourceCount;
    private final Ingredient dust;
    private final int dustCount;
    private final Ingredient crystal;
    private final int crystalCount;

    private int duration = 72;

    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private @Nullable String group;

    private ChamberRecipeBuilder(RecipeCategory category, Ingredient resource, int resourceCount,
                                 Ingredient dust, int dustCount, Ingredient crystal, int crystalCount,
                                 ItemStackTemplate result) {
        this.category = category;
        this.result = result;
        this.resource = resource;
        this.resourceCount = resourceCount;
        this.dust = dust;
        this.dustCount = dustCount;
        this.crystal = crystal;
        this.crystalCount = crystalCount;
    }

    public static ChamberRecipeBuilder crystallizerRecipe(RecipeCategory category,
                                                          Ingredient resource, int resourceCount,
                                                          Ingredient dust, int dustCount,
                                                          Ingredient crystal, int crystalCount,
                                                          ItemLike result, int resultCount) {
        return new ChamberRecipeBuilder(category, resource, resourceCount, dust, dustCount, crystal, crystalCount,
                new ItemStackTemplate(result.asItem(), resultCount));
    }

    public static ChamberRecipeBuilder crystallizerRecipe(RecipeCategory category,
                                                          Ingredient resource, int resourceCount,
                                                          Ingredient dust, int dustCount,
                                                          Ingredient crystal, int crystalCount,
                                                          ItemLike result) {
        return new ChamberRecipeBuilder(category, resource, resourceCount, dust, dustCount, crystal, crystalCount,
                new ItemStackTemplate(result.asItem()));
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

    public ChamberRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        ChamberRecipe recipe = new ChamberRecipe(resource, resourceCount, dust, dustCount, crystal, crystalCount, this.result, duration);
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }
}
