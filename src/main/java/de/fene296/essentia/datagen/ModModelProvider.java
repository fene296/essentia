package de.fene296.essentia.datagen;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Essentia.MODID);
    }

    //Add Items for .json File - Gen
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.EARTH_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SOUL_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.LIGHT_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SHADOW_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ARCANE_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TIME_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.AETHER_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHAOS_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.ESSENCE_DUST.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.PRIMORDIAL_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
    }
}
