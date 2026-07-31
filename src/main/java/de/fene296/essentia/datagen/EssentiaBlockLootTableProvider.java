package de.fene296.essentia.datagen;

import de.fene296.essentia.block.EssentiaBlocks;
import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class EssentiaBlockLootTableProvider extends BlockLootSubProvider {

    public EssentiaBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get());

        add(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get(), this::createSilkTouchOnlyTable);
        add(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get(), this::createSilkTouchOnlyTable);
        add(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get(), this::createSilkTouchOnlyTable);

        add(EssentiaBlocks.PRIMORDIAL_CLUSTER.get(), createMultipleDrops(EssentiaBlocks.PRIMORDIAL_CLUSTER.get(), EssentiaItems.PRIMORDIAL_CRYSTAL.get(), 1,2));

        dropOther(EssentiaBlocks.BUDDING_PRIMORDIAL.get(), EssentiaBlocks.DAMAGED_BUDDING_PRIMORDIAL.get());
    }

    protected LootTable.Builder createMultipleDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EssentiaBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
