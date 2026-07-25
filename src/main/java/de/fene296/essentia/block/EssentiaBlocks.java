package de.fene296.essentia.block;

import de.fene296.essentia.Essentia;
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

    public static final DeferredBlock<Block> BUDDING_PRIMORDIAL = registerBlock(
            "budding_primordial",
            properties -> new Block(properties.strength(32f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
            );

    public static final DeferredBlock<Block> DAMAGED_BUDDING_PRIMORDIAL = registerBlock(
            "damaged_budding_primordial",
            properties -> new Block(properties.strength(5f).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
    );

    public static ResourceKey<Block> getResourceKey(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
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
