package de.fene296.essentia.screen;

import de.fene296.essentia.Essentia;
import de.fene296.essentia.screen.custom.essence_burner.EssenceBurnerMenu;
import de.fene296.essentia.screen.custom.essence_extractor.EssenceExtractorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EssentiaMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Essentia.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<EssenceExtractorMenu>> ESSENCE_EXTRACTOR_MENU = registerMenuType("essence_extractor_menu", EssenceExtractorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<EssenceBurnerMenu>> ESSENCE_BURNER_MENU = registerMenuType("essence_burner_menu", EssenceBurnerMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
