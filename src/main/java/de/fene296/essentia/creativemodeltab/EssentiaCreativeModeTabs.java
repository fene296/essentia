package de.fene296.essentia.creativemodeltab;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.item.EssentiaItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EssentiaCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Essentia.MODID);

    public static final Supplier<CreativeModeTab> ESSENTIA_TAB = CREATIVE_MODE_TABS.register("essentia_creative_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(EssentiaItems.CHAOS_ESSENCE_CRYSTAL.get()))
            .title(Component.translatable("creativetab.essentia"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(EssentiaItems.CHAOS_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.AETHER_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.ARCANE_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.LIGHT_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.EARTH_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.SOUL_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.TIME_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.SHADOW_ESSENCE_CRYSTAL);
                output.accept(EssentiaItems.ESSENCE_DUST);
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
