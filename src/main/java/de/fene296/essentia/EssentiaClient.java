package de.fene296.essentia;

import de.fene296.essentia.block.EssentiaBlockEntities;
import de.fene296.essentia.block.entity.renderer.essence_extractor.EssenceExtractorEntityRenderer;
import de.fene296.essentia.screen.EssentiaMenuTypes;
import de.fene296.essentia.screen.custom.essence_extractor.EssenceExtractorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Essentia.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Essentia.MODID, value = Dist.CLIENT)
public class EssentiaClient {
    public EssentiaClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Essentia.LOGGER.info("HELLO FROM CLIENT SETUP");
        Essentia.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EssentiaBlockEntities.ESSENCE_EXTRACTOR_BE.get(), EssenceExtractorEntityRenderer::new);
    }

    //Register Screens
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(EssentiaMenuTypes.ESSENCE_EXTRACTOR_MENU.get(), EssenceExtractorScreen::new);
    }
}
