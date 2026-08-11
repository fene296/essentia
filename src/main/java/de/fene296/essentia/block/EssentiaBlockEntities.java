package de.fene296.essentia.block;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.entity.EssenceExtractorEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registers all of Essentia's {@link BlockEntityType}s (the "type" objects Minecraft
 * uses to know which block entity class belongs to which block, and to serialize/
 * deserialize them correctly).
 * <p>
 * Uses NeoForge's {@link DeferredRegister} pattern: entries are only actually added
 * to the registry once {@link #register(IEventBus)} is called during mod setup
 * (see {@code Essentia}'s constructor), even though the fields themselves are
 * initialized as soon as this class is loaded.
 */
public class EssentiaBlockEntities {

    /** The deferred registry all of this mod's block entity types get registered into. */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Essentia.MODID);


    public static final Supplier<BlockEntityType<EssenceExtractorEntity>> ESSENCE_EXTRACTOR_BE = BLOCK_ENTITIES.register("essence_extractor_be",
            () -> new BlockEntityType<>(EssenceExtractorEntity::new, EssentiaBlocks.ESSENCE_EXTRACTOR.get()));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}