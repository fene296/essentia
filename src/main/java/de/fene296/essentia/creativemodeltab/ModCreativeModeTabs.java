package de.fene296.essentia.creativemodeltab;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.block.EssentiaBlocks;
import de.fene296.essentia.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Essentia.MODID);

    public static final Supplier<CreativeModeTab> ESSENTIA_TAB = CREATIVE_MODE_TABS.register("essentia_creative_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.CHAOS_ESSENCE_CRYSTAL.get()))
            .title(Component.translatable("creativetab.essentia"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.CHAOS_ESSENCE_CRYSTAL);
                output.accept(ModItems.AETHER_ESSENCE_CRYSTAL);
                output.accept(ModItems.ARCANE_ESSENCE_CRYSTAL);
                output.accept(ModItems.LIGHT_ESSENCE_CRYSTAL);
                output.accept(ModItems.EARTH_ESSENCE_CRYSTAL);
                output.accept(ModItems.SOUL_ESSENCE_CRYSTAL);
                output.accept(ModItems.TIME_ESSENCE_CRYSTAL);
                output.accept(ModItems.SHADOW_ESSENCE_CRYSTAL);

                output.accept(ModItems.ESSENCE_DUST);

                output.accept(ModItems.PRIMORDIAL_CRYSTAL);

                output.accept(EssentiaBlocks.SMALL_PRIMORDIAL_BUD);
                output.accept(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD);
                output.accept(EssentiaBlocks.LARGE_PRIMORDIAL_BUD);
                output.accept(EssentiaBlocks.PRIMORDIAL_CRYSTAL_CLUSTER);
                output.accept(EssentiaBlocks.BUDDING_PRIMORDIAL_CRYSTAL_BLOCK);
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
