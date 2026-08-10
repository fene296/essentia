package de.fene296.essentia.recipe;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record ChamberRecipe(Ingredient inputItem, ItemStackTemplate output) implements Recipe<ChamberRecipeInput> {

    public static final MapCodec<ChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(ChamberRecipe::inputItem),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(ChamberRecipe::output)
            ).apply(instance, ChamberRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChamberRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    ChamberRecipe::inputItem,

                    ItemStackTemplate.STREAM_CODEC,
                    ChamberRecipe::output,

                    ChamberRecipe::new);

    @Override
    public boolean matches(ChamberRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ChamberRecipeInput input) {
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
    public RecipeSerializer<? extends Recipe<ChamberRecipeInput>> getSerializer() {
        return EssentiaRecipes.CHAMBER_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<ChamberRecipeInput>> getType() {
        return EssentiaRecipes.CHAMBER_TYPE.get();
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
