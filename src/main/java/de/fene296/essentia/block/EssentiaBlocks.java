package de.fene296.essentia.block;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.custom.BuddingPrimordial;
import de.fene296.essentia.block.custom.DamagedBuddingPrimordial;
import de.fene296.essentia.block.custom.EssenceBurner;
import de.fene296.essentia.block.custom.EssenceExtractor;
import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class EssentiaBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Essentia.MODID);

    public static final DeferredBlock<BuddingPrimordial> BUDDING_PRIMORDIAL = registerBlock(
            "budding_primordial",
            properties -> new BuddingPrimordial(properties.strength(32f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST).randomTicks())
            );

    public static final DeferredBlock<DamagedBuddingPrimordial> DAMAGED_BUDDING_PRIMORDIAL = registerBlock(
            "damaged_budding_primordial",
            properties -> new DamagedBuddingPrimordial(properties.strength(5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST).randomTicks())
    );

    public static final DeferredBlock<EssenceExtractor> ESSENCE_EXTRACTOR = registerBlock(
            "essence_extractor",
            properties -> new EssenceExtractor(properties.strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE))
    );

    public static final DeferredBlock<EssenceBurner> ESSENCE_BURNER = registerBlock(
            "essence_burner",
            properties -> new EssenceBurner(properties.strength(5f).requiresCorrectToolForDrops().sound(SoundType.STONE))
    );


    public static final DeferredBlock<AmethystClusterBlock> SMALL_PRIMORDIAL_BUD = registerBlock("small_primordial_bud",
            properties -> new AmethystClusterBlock(3,8, budProperties(properties)));

    public static final DeferredBlock<AmethystClusterBlock> MEDIUM_PRIMORDIAL_BUD = registerBlock("medium_primordial_bud",
            properties -> new AmethystClusterBlock(5,8, budProperties(properties)));

    public static final DeferredBlock<AmethystClusterBlock> LARGE_PRIMORDIAL_BUD = registerBlock("large_primordial_bud",
            properties -> new AmethystClusterBlock(7,10, budProperties(properties)));

    public static final DeferredBlock<AmethystClusterBlock> PRIMORDIAL_CLUSTER = registerBlock("primordial_cluster",
            properties -> new AmethystClusterBlock(9,12, budProperties(properties)));

    public static ResourceKey<Block> getResourceKey(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }

    private static BlockBehaviour.Properties budProperties(BlockBehaviour.Properties properties) {
        return properties.strength(1.5f).sound(SoundType.AMETHYST_CLUSTER).requiresCorrectToolForDrops();
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registeBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registeBlockItem(String name, DeferredBlock<T> block) {
        EssentiaItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
