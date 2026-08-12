package de.fene296.essentia.block.entity.renderer.essence_extractor;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.Level;

public class EssenceExtractorEntityRenderState extends BlockEntityRenderState {
    public Level level;
    public float rotation;

    public final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}
