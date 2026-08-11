package de.fene296.essentia.recipe.essence_extractor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * The 3 ingredient slots (resource, essence dust, primordial crystal) passed to
 * {@link EssenceExtractorRecipe} for matching. Each {@link ItemStack} reflects the
 * actual current contents of that slot (item + real amount), not just "is it filled".
 * <p>
 * Built fresh each tick from the block entity's inventory - see
 * {@code EssenceExtractorEntity#currentInput()}.
 */
public record EssenceExtractorRecipeInput(ItemStack resource, ItemStack dust, ItemStack crystal) implements RecipeInput {

    /**
     * Maps a slot index (0/1/2) to the corresponding stack, as required by the
     * generic {@link RecipeInput} interface.
     */
    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> resource;
            case 1 -> dust;
            case 2 -> crystal;
            default -> throw new IllegalArgumentException("Invalid chamber recipe slot index: " + i);
        };
    }

    /** Always 3: resource, dust, crystal. */
    @Override
    public int size() {
        return 3;
    }
}