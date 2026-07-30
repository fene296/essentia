package de.fene296.essentia.datagen;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.EssentiaBlocks;
import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Essentia.MODID);
    }

    //Add Items for .json File - Gen
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        //Items
        itemModels.generateFlatItem(EssentiaItems.EARTH_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.SOUL_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.LIGHT_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.SHADOW_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.ARCANE_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.TIME_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.AETHER_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.CHAOS_ESSENCE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EssentiaItems.ESSENCE_DUST.get(), ModelTemplates.FLAT_ITEM);

        //Blocks
        blockModels.createTrivialCube(EssentiaBlocks.BUDDING_PRIMORDIAL.get());
        blockModels.createTrivialCube(EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get());

        blockModels.createAmethystCluster(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get());
        blockModels.createAmethystCluster(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get());
        blockModels.createAmethystCluster(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get());
        blockModels.createAmethystCluster(EssentiaBlocks.PRIMORDIAL_CLUSTER.get());
    }
}
