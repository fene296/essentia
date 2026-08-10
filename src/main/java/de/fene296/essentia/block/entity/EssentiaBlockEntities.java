package de.fene296.essentia.block.entity;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.EssentiaBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EssentiaBlockEntities {

    public static  final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Essentia.MODID);

    public static final Supplier<BlockEntityType<EssenceExtractorEntity>> ESSENCE_EXTRACTOR_BE = BLOCK_ENTITIES.register("essence_extractor_be",
            () -> new BlockEntityType<>(
            EssenceExtractorEntity::new, EssentiaBlocks.ESSENCE_EXTRACTOR.get()));

    public static final Supplier<BlockEntityType<ChamberBlockEntity>> CHAMBER_BE = BLOCK_ENTITIES.register("chamber_be",
            () -> new BlockEntityType<>(
                    ChamberBlockEntity::new, EssentiaBlocks.CHAMBER.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
