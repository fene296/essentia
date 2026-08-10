package de.fene296.essentia;

import de.fene296.essentia.datagen.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = Essentia.MODID)
public class EssentiaDataGen {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new EssentiaModelProvider(packOutput));

        generator.addProvider(true, new EssentiaBlockTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new EssentiaItemTagsProvider(packOutput, lookupProvider));

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(EssentiaBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new EssentiaRecipeProvider.Runner(packOutput, lookupProvider));
    }
}
