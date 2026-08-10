package de.fene296.essentia.recipe;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record ChamberRecipe(
        Ingredient resource, int resourceCount,
        Ingredient dust, int dustCount,
        Ingredient crystal, int crystalCount,
        ItemStackTemplate output,
        int duration
) implements Recipe<ChamberRecipeInput> {

    public static final MapCodec<ChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("resource").forGetter(ChamberRecipe::resource),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("resourceCount", 1).forGetter(ChamberRecipe::resourceCount),
                    Ingredient.CODEC.fieldOf("dust").forGetter(ChamberRecipe::dust),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("dustCount", 1).forGetter(ChamberRecipe::dustCount),
                    Ingredient.CODEC.fieldOf("crystal").forGetter(ChamberRecipe::crystal),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("crystalCount", 1).forGetter(ChamberRecipe::crystalCount),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(ChamberRecipe::output),
                    com.mojang.serialization.Codec.INT.optionalFieldOf("duration", 72).forGetter(ChamberRecipe::duration)
            ).apply(instance, ChamberRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChamberRecipe> STREAM_CODEC =
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
                        return new ChamberRecipe(resource, resourceCount, dust, dustCount, crystal, crystalCount, output, duration);
                    }
            );

    @Override
    public boolean matches(ChamberRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return resource.test(input.resource()) && input.resource().getCount() >= resourceCount
                && dust.test(input.dust()) && input.dust().getCount() >= dustCount
                && crystal.test(input.crystal()) && input.crystal().getCount() >= crystalCount;
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
