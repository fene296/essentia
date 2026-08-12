package de.fene296.essentia.datagen;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.EssentiaBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class EssentiaBlockTagsProvider extends BlockTagsProvider {

    public EssentiaBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Essentia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.BUDDING_PRIMORDIAL.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.PRIMORDIAL_CLUSTER.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.ESSENCE_EXTRACTOR.get()));

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.BUDDING_PRIMORDIAL.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get()))
                .add(EssentiaBlocks.getResourceKey(EssentiaBlocks.PRIMORDIAL_CLUSTER.get()));

    }
}
