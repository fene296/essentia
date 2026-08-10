package de.fene296.essentia.recipe;

import de.fene296.essentia.Essentia;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EssentiaRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Essentia.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Essentia.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChamberRecipe>> CHAMBER_SERIALIZER =
            SERIALIZERS.register("chamber", () -> new RecipeSerializer<>(ChamberRecipe.CODEC, ChamberRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChamberRecipe>> CHAMBER_TYPE =
            TYPES.register("chamber", () -> new RecipeType<ChamberRecipe>() {
                @Override
                public String toString() {
                    return "chamber";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
