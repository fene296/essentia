package de.fene296.essentia.datagen;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.tags.EssentiaTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class EssentiaItemTagsProvider extends ItemTagsProvider {

    public EssentiaItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Essentia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EssentiaTags.Items.MAGIC_DUST)
                .add(EssentiaItems.getResourceKey(EssentiaItems.ESSENCE_DUST.get()))
                .add(ItemIds.GLOWSTONE_DUST);
    }
}