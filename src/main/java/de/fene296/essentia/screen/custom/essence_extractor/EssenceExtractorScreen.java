package de.fene296.essentia.screen.custom.essence_extractor;

import de.fene296.essentia.Essentia;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side GUI screen for the Essence Extractor machine.
 * <p>
 * Draws the machine's background texture and, while a craft is in progress, an
 * animated progress arrow between the input slots and the output slot.
 * <p>
 * {@code imageWidth}/{@code imageHeight} (used below to center the GUI on screen)
 * come from the parent {@link AbstractContainerScreen} and are normally derived
 * from the background texture's intended display size.
 */
public class EssenceExtractorScreen extends AbstractContainerScreen<EssenceExtractorMenu> {

    /** The main GUI background texture (slots, borders, etc.). */
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(Essentia.MODID,"textures/gui/essence_extractor/essence_extractor.png");

    /** Texture strip used for the animated crafting-progress arrow. */
    private static final Identifier ARROW_TEXTURE =
            Identifier.fromNamespaceAndPath(Essentia.MODID,"textures/gui/progress.png");

    public EssenceExtractorScreen(EssenceExtractorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /**
     * Draws the GUI's background layer (behind the slots/items/tooltips), called
     * every frame. This is the modern replacement for the older {@code renderBg}
     * method: it takes a {@link GuiGraphicsExtractor} instead of {@code GuiGraphics},
     * and textures are drawn via {@code blit(RenderPipelines.GUI_TEXTURED, ...)}.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draws the full GUI_TEXTURE (256x256 source image) at (x, y), showing the
        // top-left imageWidth x imageHeight region of it.
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderProgressArrow(graphics, x, y);
    }

    /**
     * Draws the crafting-progress arrow, clipped to the current progress width
     * ({@link EssenceExtractorMenu#getScaledArrowProgress()}), only while a craft
     * is actively running.
     */
    private void renderProgressArrow(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE,x + 96, y + 35, 0, 0,
                    menu.getScaledArrowProgress(), 16, 24, 16);
        }
    }
}