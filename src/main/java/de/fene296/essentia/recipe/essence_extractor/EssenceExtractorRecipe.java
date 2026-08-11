package de.fene296.essentia.recipe.essence_extractor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.fene296.essentia.recipe.EssentiaRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * The recipe type for the Essence Extractor machine: converts a resource item +
 * essence dust + a primordial crystal into a resulting essence crystal.
 * <p>
 * Each ingredient can require more than 1 item ({@code resourceCount}, {@code dustCount},
 * {@code crystalCount}, all default to 1 if omitted in the recipe JSON), and crafting
 * takes {@code duration} ticks (default 72 = 3.6 seconds).
 * <p>
 * Uses {@link ItemStackTemplate} rather than a plain {@link ItemStack} for the output,
 * since a template describes "what item + count + components to produce" without being
 * a live stack instance itself - the actual {@link ItemStack} is only created when
 * {@link #assemble} is called.
 */
public record EssenceExtractorRecipe (
        Ingredient resource, int resourceCount,
        Ingredient dust, int dustCount,
        Ingredient crystal, int crystalCount,
        ItemStackTemplate output,
        int duration
) implements Recipe<EssenceExtractorRecipeInput> {

    /**
     * JSON (data pack) codec: describes how this recipe is read from / written to
     * the recipe's JSON file. Field order here must match the record's constructor
     * parameter order. All 3 count fields and {@code duration} are optional and
     * default to 1 (counts) / 72 (duration) if left out of the JSON.
     */
    public static final MapCodec<EssenceExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("resource").forGetter(EssenceExtractorRecipe::resource),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("resourceCount", 1).forGetter(EssenceExtractorRecipe::resourceCount),
                    Ingredient.CODEC.fieldOf("dust").forGetter(EssenceExtractorRecipe::dust),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("dustCount", 1).forGetter(EssenceExtractorRecipe::dustCount),
                    Ingredient.CODEC.fieldOf("crystal").forGetter(EssenceExtractorRecipe::crystal),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("crystalCount", 1).forGetter(EssenceExtractorRecipe::crystalCount),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(EssenceExtractorRecipe::output),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("duration", 72).forGetter(EssenceExtractorRecipe::duration)
            ).apply(instance, EssenceExtractorRecipe::new));

    /**
     * Network codec: describes how this recipe is sent from server to client (e.g. for
     * the recipe book / sync on join). Written manually (rather than via the usual
     * {@code StreamCodec.composite(...)} builder) because that helper only supports up
     * to 6 fields, and this recipe has 8 (3 ingredient+count pairs, output, duration).
     * Encode/decode order must match exactly on both sides.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceExtractorRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, recipe) -> {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.resource);
                        ByteBufCodecs.VAR_INT.encode(buf, recipe.resourceCount);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.dust);
                        ByteBufCodecs.VAR_INT.encode(buf, recipe.dustCount);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.crystal);
                        ByteBufCodecs.VAR_INT.encode(buf, recipe.crystalCount);
                        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
                        ByteBufCodecs.VAR_INT.encode(buf, recipe.duration);
                    },
                    (buf) -> {
                        Ingredient resource = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int resourceCount = ByteBufCodecs.VAR_INT.decode(buf);
                        Ingredient dust = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int dustCount = ByteBufCodecs.VAR_INT.decode(buf);
                        Ingredient crystal = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int crystalCount = ByteBufCodecs.VAR_INT.decode(buf);
                        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buf);
                        int duration = ByteBufCodecs.VAR_INT.decode(buf);
                        return new EssenceExtractorRecipe(resource, resourceCount, dust, dustCount, crystal, crystalCount, output, duration);
                    }
            );

    /**
     * Checks whether the given input slots (resource/dust/crystal, with their actual
     * current item + amount) satisfy this recipe: each slot's item must match the
     * corresponding {@link Ingredient}, and have at least the required count.
     * <p>
     * Always returns {@code false} on the client, since recipe matching only needs
     * to happen server-side (the client just displays synced crafting progress).
     */
    @Override
    public boolean matches(EssenceExtractorRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return resource.test(input.resource()) && input.resource().getCount() >= resourceCount
                && dust.test(input.dust()) && input.dust().getCount() >= dustCount
                && crystal.test(input.crystal()) && input.crystal().getCount() >= crystalCount;
    }

    /** Produces the actual result {@link ItemStack} for this recipe, from its output template. */
    @Override
    public ItemStack assemble(EssenceExtractorRecipeInput input) {
        return output.create().copy();
    }

    /** Whether completing this recipe for the first time shows the recipe-unlock toast notification. */
    @Override
    public boolean showNotification() {
        return true;
    }

    /**
     * Recipe book "group" name used to fold visually-similar recipes together in the UI.
     * NOTE: still says "Chamber" here, likely left over from the original Chamber recipe
     * this was adapted from - probably worth renaming to match the Essence Extractor.
     */
    @Override
    public String group() {
        return "Chamber";
    }

    @Override
    public RecipeSerializer<? extends Recipe<EssenceExtractorRecipeInput>> getSerializer() {
        return EssentiaRecipes.ESSENCE_EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<EssenceExtractorRecipeInput>> getType() {
        return EssentiaRecipes.ESSENCE_EXTRACTOR_TYPE.get();
    }

    /**
     * Describes where/how this recipe can be "placed" for recipe-book/ghost-slot purposes.
     * {@code NOT_PLACEABLE} since this isn't a crafting-table-style recipe the recipe book
     * can auto-fill slots for.
     */
    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    /** Which recipe book tab/category this recipe shows up under. */
    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}