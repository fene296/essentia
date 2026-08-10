package de.fene296.essentia.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ChamberRecipeInput(ItemStack resource, ItemStack dust, ItemStack crystal) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> resource;
            case 1 -> dust;
            case 2 -> crystal;
            default -> throw new IllegalArgumentException("Invalid chamber recipe slot index: " + i);
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
