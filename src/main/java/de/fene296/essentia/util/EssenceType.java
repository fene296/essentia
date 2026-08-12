package de.fene296.essentia.util;

import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Arrays;
import java.util.Optional;

public enum EssenceType {
    EARTH(EssentiaItems.EARTH_ESSENCE_CRYSTAL),
    SOUL(EssentiaItems.SOUL_ESSENCE_CRYSTAL),
    LIGHT(EssentiaItems.LIGHT_ESSENCE_CRYSTAL),
    SHADOW(EssentiaItems.SHADOW_ESSENCE_CRYSTAL),
    ARCANE(EssentiaItems.ARCANE_ESSENCE_CRYSTAL),
    TIME(EssentiaItems.TIME_ESSENCE_CRYSTAL),
    AETHER(EssentiaItems.AETHER_ESSENCE_CRYSTAL),
    CHAOS(EssentiaItems.CHAOS_ESSENCE_CRYSTAL);

    private final DeferredItem<Item> crystalItem;

    EssenceType(DeferredItem<Item> crystalItem) {
        this.crystalItem = crystalItem;
    }

    public Item getCrystalItem() {
        return crystalItem.get();
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
