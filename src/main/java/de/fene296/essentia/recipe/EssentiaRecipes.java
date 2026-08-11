package de.fene296.essentia.recipe;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.recipe.essence_extractor.EssenceExtractorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EssentiaRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Essentia.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Essentia.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EssenceExtractorRecipe>> ESSENCE_EXTRACTOR_SERIALIZER =
            SERIALIZERS.register("essence_extractor", () -> new RecipeSerializer<>(EssenceExtractorRecipe.CODEC, EssenceExtractorRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<EssenceExtractorRecipe>> ESSENCE_EXTRACTOR_TYPE =
            TYPES.register("essence_extractor", () -> new RecipeType<EssenceExtractorRecipe>() {
                @Override
                public String toString() {
                    return "essence_extractor";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
