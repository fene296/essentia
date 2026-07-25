package de.fene296.essentia.datagen;

import de.fene296.essentia.block.EssentiaBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get());

        //add(//Block, createOreDrop(org, drop);
        dropOther(EssentiaBlocks.BUDDING_PRIMORDIAL.get(), EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EssentiaBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
