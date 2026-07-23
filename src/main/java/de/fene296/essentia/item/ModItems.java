package de.fene296.essentia.item;

import de.fene296.essentia.Essentia;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Essentia.MODID);

    public static final DeferredItem<Item> EARTH_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("earth_essence_crystal");
    public static final DeferredItem<Item> SOUL_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("soul_essence_crystal");
    public static final DeferredItem<Item> LIGHT_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("light_essence_crystal");
    public static final DeferredItem<Item> SHADOW_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("shadow_essence_crystal");
    public static final DeferredItem<Item> ARCANE_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("arcane_essence_crystal");
    public static final DeferredItem<Item> TIME_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("time_essence_crystal");
    public static final DeferredItem<Item> AETHER_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("aether_essence_crystal");
    public static final DeferredItem<Item> CHAOS_ESSENCE_CRYSTAL = ITEMS.registerSimpleItem("chaos_essence_crystal");

    public static final DeferredItem<Item> ESSENCE_DUST = ITEMS.registerSimpleItem("essence_dust");

    public static final DeferredItem<Item> PRIMORDIAL_CRYSTAL = ITEMS.registerSimpleItem("primordial_crystal");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


}
