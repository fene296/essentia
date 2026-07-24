package de.fene296.essentia.block;

import de.fene296.essentia.Essentia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EssentiaBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Essentia.MODID);

    public static final DeferredBlock<BuddingPrimordialCrystalBlock> BUDDING_PRIMORDIAL_CRYSTAL_BLOCK = BLOCKS.register(
            "budding_primordial_crystal",
            key -> new BuddingPrimordialCrystalBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, key))
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.5f)
                    .sound(SoundType.AMETHYST)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<AmethystClusterBlock> SMALL_PRIMORDIAL_BUD = BLOCKS.register(
            "small_primordial_bud",
            key -> new AmethystClusterBlock(3,4,budProperties(key))
    );

    public static final DeferredBlock<AmethystClusterBlock> MEDIUM_PRIMORDIAL_BUD = BLOCKS.register(
            "medium_primordial_bud",
            key -> new AmethystClusterBlock(4,3,budProperties(key))
    );

    public static final DeferredBlock<AmethystClusterBlock> LARGE_PRIMORDIAL_BUD = BLOCKS.register(
            "large_primordial_bud",
            key -> new AmethystClusterBlock(5,3,budProperties(key))
    );

    public static final DeferredBlock<AmethystClusterBlock> PRIMORDIAL_CRYSTAL_CLUSTER = BLOCKS.register(
            "primordial_crystal_cluster",
            key -> new AmethystClusterBlock(7,3, budProperties(key).lightLevel(state -> 6))
    );

    private static BlockBehaviour.Properties budProperties(Identifier key) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, key))
                .mapColor(MapColor.COLOR_PURPLE)
                .noCollision()
                .strength(1.5f)
                .sound(SoundType.AMETHYST_CLUSTER)
                .pushReaction(PushReaction.DESTROY);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
