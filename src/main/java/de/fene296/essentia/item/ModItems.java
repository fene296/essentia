package de.fene296.essentia.item;

import de.fene296.essentia.Essentia;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Essentia.MODID);

    public static final DeferredItem<Item> EARTH_ESSENCE = ITEMS.registerSimpleItem("earth_essence");
    public static final DeferredItem<Item> LIFE_ESSENCE = ITEMS.registerSimpleItem("life_essence");
    public static final DeferredItem<Item> ARCANE_ESSENCE = ITEMS.registerSimpleItem("arcane_essence");
    public static final DeferredItem<Item> LIGHT_ESSENCE = ITEMS.registerSimpleItem("light_essence");
    public static final DeferredItem<Item> DARK_ESSENCE = ITEMS.registerSimpleItem("dark_essence");
    public static final DeferredItem<Item> SOUL_ESSENCE = ITEMS.registerSimpleItem("soul_essence");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


}
