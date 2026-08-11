package de.fene296.essentia.recipe.essence_extractor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.fene296.essentia.recipe.EssentiaRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record EssenceExtractorRecipe (
        Ingredient resource, int resourceCount,
        Ingredient dust, int dustCount,
        Ingredient crystal, int crystalCount,
        ItemStackTemplate output,
        int duration
) implements Recipe<EssenceExtractorRecipeInput> {

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

    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceExtractorRecipe> STREAM_CODEC =
            StreamCodec.of(
                    (buf, recipe) -> {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.resource);
                        net.minecraft.network.codec.ByteBufCodecs.VAR_INT.encode(buf, recipe.resourceCount);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.dust);
                        net.minecraft.network.codec.ByteBufCodecs.VAR_INT.encode(buf, recipe.dustCount);
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.crystal);
                        net.minecraft.network.codec.ByteBufCodecs.VAR_INT.encode(buf, recipe.crystalCount);
                        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
                        net.minecraft.network.codec.ByteBufCodecs.VAR_INT.encode(buf, recipe.duration);
                    },
                    (buf) -> {
                        Ingredient resource = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int resourceCount = net.minecraft.network.codec.ByteBufCodecs.VAR_INT.decode(buf);
                        Ingredient dust = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int dustCount = net.minecraft.network.codec.ByteBufCodecs.VAR_INT.decode(buf);
                        Ingredient crystal = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                        int crystalCount = net.minecraft.network.codec.ByteBufCodecs.VAR_INT.decode(buf);
                        ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buf);
                        int duration = net.minecraft.network.codec.ByteBufCodecs.VAR_INT.decode(buf);
                        return new EssenceExtractorRecipe(resource, resourceCount, dust, dustCount, crystal, crystalCount, output, duration);
                    }
            );

    @Override
    public boolean matches(EssenceExtractorRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return resource.test(input.resource()) && input.resource().getCount() >= resourceCount
                && dust.test(input.dust()) && input.dust().getCount() >= dustCount
                && crystal.test(input.crystal()) && input.crystal().getCount() >= crystalCount;
    }

    @Override
    public ItemStack assemble(EssenceExtractorRecipeInput input) {
        return output.create().copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

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

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}