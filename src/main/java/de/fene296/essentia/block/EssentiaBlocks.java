package de.fene296.essentia.block;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class EssentiaBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Essentia.MODID);

    public static final DeferredBlock<BuddingPrimordialCrystalBlock> BUDDING_PRIMORDIAL_CRYSTAL_BLOCK = registerBlock(
            "budding_primordial_crystal",
            properties -> new BuddingPrimordialCrystalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.5f)
                    .sound(SoundType.AMETHYST)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final DeferredBlock<AmethystClusterBlock> SMALL_PRIMORDIAL_BUD = registerBlock(
            "small_primordial_bud",
            key -> new AmethystClusterBlock(3,4,budProperties())
    );

    public static final DeferredBlock<AmethystClusterBlock> MEDIUM_PRIMORDIAL_BUD = registerBlock(
            "medium_primordial_bud",
            key -> new AmethystClusterBlock(4,3,budProperties())
    );

    public static final DeferredBlock<AmethystClusterBlock> LARGE_PRIMORDIAL_BUD = registerBlock(
            "large_primordial_bud",
            key -> new AmethystClusterBlock(5,3,budProperties())
    );

    public static final DeferredBlock<AmethystClusterBlock> PRIMORDIAL_CRYSTAL_CLUSTER = registerBlock(
            "primordial_crystal_cluster",
            key -> new AmethystClusterBlock(7,3, budProperties().lightLevel(state -> 6))
    );

    private static BlockBehaviour.Properties budProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .noCollision()
                .strength(1.5f)
                .sound(SoundType.AMETHYST_CLUSTER)
                .pushReaction(PushReaction.DESTROY);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, key -> function.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, key))));
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, key -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, key)).useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
