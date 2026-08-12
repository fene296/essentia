package de.fene296.essentia.tags;

import de.fene296.essentia.Essentia;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EssentiaTags {

    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(Essentia.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> MAGIC_DUST = createTag("magic_dust");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(Essentia.MODID, name));
        }
    }
}
