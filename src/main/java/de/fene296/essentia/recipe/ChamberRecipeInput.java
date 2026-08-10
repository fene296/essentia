package de.fene296.essentia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ChamberRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
