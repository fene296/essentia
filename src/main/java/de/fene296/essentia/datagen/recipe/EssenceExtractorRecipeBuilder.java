package de.fene296.essentia.datagen.recipe;

import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.recipe.essence_extractor.EssenceExtractorRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

/**
 * Builder for {@link EssenceExtractorRecipe}s, used from {@code EssentiaRecipeProvider}.
 * <p>
 * Only {@code resource} and {@code result} are required (passed to
 * {@link #extractRecipe(RecipeCategory, Item, int, ItemLike, int)} below). {@code dust}
 * and {@code crystal} default to 1x Essence Dust / 1x Primordial Crystal - matching
 * what every recipe on this machine uses anyway - but can be overridden per-recipe via
 * {@link #dust(Item, int)} / {@link #crystal(Item, int)}, the same fluent-optional
 * pattern as {@link #duration(int)}.
 */
public class EssenceExtractorRecipeBuilder implements RecipeBuilder {

    /** Datagen-only grouping used for the advancement icon/description (see the note in a previous discussion - doesn't affect any in-game recipe book UI here). */
    private final RecipeCategory category;

    /** The crafted result (item + count), stored as a template rather than a live stack. */
    private final ItemStackTemplate result;

    /** The main "resource" ingredient (e.g. Dirt) and how many of it are consumed per craft. */
    private final Ingredient resource;
    private final int resourceCount;

    /** Essence Dust requirement - defaults to 1x Essence Dust unless overridden via {@link #dust(Item, int)}. */
    private Ingredient dust = Ingredient.of(EssentiaItems.ESSENCE_DUST.get());
    private int dustCount = 1;

    /** Primordial Crystal requirement - defaults to 1x Primordial Crystal unless overridden via {@link #crystal(Item, int)}. */
    private Ingredient crystal = Ingredient.of(EssentiaItems.PRIMORDIAL_CRYSTAL.get());
    private int crystalCount = 1;

    /** How many ticks (20 = 1 second) crafting takes. Defaults to 72 (3.6s) unless overridden via {@link #duration(int)}. */
    private int duration = 72;

    /** Collects the unlock criteria (e.g. "has_dirt") for the recipe-unlock advancement. */
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();

    /** Optional recipe book grouping name, set via {@link #group(String)}. */
    private @Nullable String group;

    private EssenceExtractorRecipeBuilder(RecipeCategory category, Ingredient resource, int resourceCount, ItemStackTemplate result) {
        this.category = category;
        this.result = result;
        this.resource = resource;
        this.resourceCount = resourceCount;
    }

    /**
     * Starts building a new Essence Extractor recipe.
     *
     * @param resource     the main ingredient consumed (e.g. Dirt)
     * @param resourceCount how many of {@code resource} are needed per craft
     * @param result       the item produced
     * @param resultCount  how many of {@code result} are produced per craft
     */
    public static EssenceExtractorRecipeBuilder extractRecipe(RecipeCategory category,
                                                              Item resource, int resourceCount,
                                                              ItemLike result, int resultCount) {
        return new EssenceExtractorRecipeBuilder(category, Ingredient.of(resource), resourceCount,
                new ItemStackTemplate(result.asItem(), resultCount));
    }

    /** Registers an unlock criterion (e.g. "player has picked up X") for the recipe-unlock advancement. */
    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        advancementBuilder.unlockedBy(name, criterion);
        return this;
    }

    /** Sets the recipe book grouping name (folds visually-similar recipes together in the UI). */
    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    /** Optional: override the required essence dust ingredient/amount (defaults to 1x Essence Dust). */
    public EssenceExtractorRecipeBuilder dust(Item dust, int dustCount) {
        this.dust = Ingredient.of(dust);
        this.dustCount = dustCount;
        return this;
    }

    /** Optional: override the required primordial crystal ingredient/amount (defaults to 1x Primordial Crystal). */
    public EssenceExtractorRecipeBuilder crystal(Item crystal, int crystalCount) {
        this.crystal = Ingredient.of(crystal);
        this.crystalCount = crystalCount;
        return this;
    }

    /** Optional: how many ticks (20 = 1 second) crafting this recipe takes. Defaults to 72 if not set. */
    public EssenceExtractorRecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    /** Derives the recipe's file/registry ID automatically from the result item, unless an explicit ID is passed to {@link #save(RecipeOutput, ResourceKey)}. */
    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    /** Builds the final {@link EssenceExtractorRecipe} from everything configured above and hands it (plus its unlock advancement) to the datagen output. */
    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        EssenceExtractorRecipe recipe = new EssenceExtractorRecipe(resource, resourceCount, dust, dustCount, crystal, crystalCount, this.result, duration);
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }
}