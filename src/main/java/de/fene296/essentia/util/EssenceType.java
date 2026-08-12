package de.fene296.essentia.util;

import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Arrays;
import java.util.Optional;

public enum EssenceType {
    EARTH(EssentiaItems.EARTH_ESSENCE_CRYSTAL, 0x4ADD5D),
    SOUL(EssentiaItems.SOUL_ESSENCE_CRYSTAL, 0x068388),
    LIGHT(EssentiaItems.LIGHT_ESSENCE_CRYSTAL, 0xF5E032),
    SHADOW(EssentiaItems.SHADOW_ESSENCE_CRYSTAL, 0x22024C),
    ARCANE(EssentiaItems.ARCANE_ESSENCE_CRYSTAL, 0x9C31BE),
    TIME(EssentiaItems.TIME_ESSENCE_CRYSTAL, 0x748C89),
    AETHER(EssentiaItems.AETHER_ESSENCE_CRYSTAL, 0x00F9FF),
    CHAOS(EssentiaItems.CHAOS_ESSENCE_CRYSTAL, 0xFF5900);

    private final DeferredItem<Item> crystalItem;
    private final int color;

    EssenceType(DeferredItem<Item> crystalItem, int color) {
        this.crystalItem = crystalItem;
        this.color = color;
    }

    public Item getCrystalItem() {
        return crystalItem.get();
    }

    /** Packed RGB color (0xRRGGBB) used for this essence type's particle effects. */
    public int getColor() {
        return color;
    }

    /**
     * Returns which {@link EssenceType} the given item represents (if any), by
     * matching it against each type's essence crystal item.
     */
    public static Optional<EssenceType> fromItem(Item item) {
        return Arrays.stream(values())
                .filter(type -> type.getCrystalItem() == item)
                .findFirst();
    }
}