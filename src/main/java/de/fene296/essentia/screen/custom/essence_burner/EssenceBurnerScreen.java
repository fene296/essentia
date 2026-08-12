package de.fene296.essentia.screen.custom.essence_burner;

import de.fene296.essentia.Essentia;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side GUI screen for the Essence Burner machine.
 * <p>
 * Draws the burner's background texture and, while a crystal is burning, an
 * animated furnace-style flame icon that shrinks as the burn time runs out.
 */
public class EssenceBurnerScreen extends AbstractContainerScreen<EssenceBurnerMenu> {

    /** The main GUI background texture (slot, borders, etc.). */
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(Essentia.MODID, "textures/gui/essence_burner/essence_burner.png");


    private static final Identifier FLAME_TEXTURE =
            Identifier.fromNamespaceAndPath(Essentia.MODID, "textures/gui/burn_progress.png");

    private static final int FLAME_HEIGHT = 14;

    public EssenceBurnerScreen(EssenceBurnerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /**
     * Draws the GUI's background layer, called every frame. Modern replacement for
     * the older {@code renderBg} method.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderFlame(graphics, x, y);
    }

    /**
     * Draws the burn-progress flame icon, clipped vertically (bottom-up) based on
     * remaining burn time, only while the burner is actively burning a crystal.
     */
    private void renderFlame(GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (menu.isBurning()) {
            int flameHeight = menu.getScaledBurnProgress(FLAME_HEIGHT);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, FLAME_TEXTURE,
                    x + 81, y + 48 + (FLAME_HEIGHT - flameHeight), 0, FLAME_HEIGHT - flameHeight,
                    14, flameHeight, 14, 14);
        }
    }
}

