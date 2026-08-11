package de.fene296.essentia.block.custom;

import com.mojang.serialization.MapCodec;
import de.fene296.essentia.block.EssentiaBlockEntities;
import de.fene296.essentia.block.entity.EssenceExtractorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * The Essence Extractor machine block.
 * <p>
 * This block itself only defines shape, rendering, and player interaction (right-click
 * to open the GUI, drop inventory contents when broken). The actual crafting logic and
 * item storage live in the associated {@link EssenceExtractorEntity} (the block entity),
 * which this class creates via {@link #newBlockEntity} and ticks via {@link #getTicker}.
 * <p>
 * Extends {@link BaseEntityBlock} rather than plain {@link Block} because it needs a
 * block entity (to store items and run crafting logic) and a GUI.
 */
public class EssenceExtractor extends BaseEntityBlock {

    /**
     * Codec used to (de)serialize this block's own state/identity for the block registry.
     * {@code simpleCodec(EssenceExtractor::new)} means the block only needs its
     * {@link Properties} to be reconstructed - it has no extra custom fields of its own.
     * Required by {@link BaseEntityBlock}; returned via {@link #codec()} below.
     */
    public static final MapCodec<EssenceExtractor> CODEC = simpleCodec(EssenceExtractor::new);

    /**
     * Custom collision/selection box for the block: 16 wide x 10 tall x 16 deep
     * (i.e. a full-width block that's only 10/16 of a full block tall).
     */
    public static final VoxelShape SHAPE = Block.box(0,0,0 , 16, 10, 16);

    public EssenceExtractor(Properties properties) {
        super(properties);
    }

    /**
     * Returns the codec used to store/load this block type. Required override for
     * {@link BaseEntityBlock}; always just returns the static {@link #CODEC} above.
     */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * Called by Minecraft when this block is placed, to create its associated
     * block entity (which stores the item slots and runs the crafting logic).
     */
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EssenceExtractorEntity(blockPos, blockState);
    }

    /**
     * Tells Minecraft this block should be rendered using its model (from
     * the blockstate/model JSON), not a special built-in shape like liquids use.
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Returns the custom hitbox/collision shape for this block (see {@link #SHAPE}),
     * instead of the default full 16x16x16 cube.
     */
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * Called when a player breaks this block in survival mode.
     * <p>
     * Since the machine can be holding items (resource/dust/crystal/output) when
     * destroyed, we make sure to spill those contents into the world via
     * {@link EssenceExtractorEntity#drops()} before letting the normal block-break
     * logic (item drop of the block itself, etc.) run via {@code super}.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof EssenceExtractorEntity entity) {
            entity.drops();
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    /**
     * Called when a player right-clicks this block with an empty hand (or a hand
     * whose item doesn't handle the click itself).
     * <p>
     * Opens the machine's GUI for the player, server-side only (the {@code isClientSide}
     * check prevents the menu from also trying to open on the client, which would
     * desync/duplicate the open request). The block entity itself acts as the
     * {@code MenuProvider}, wrapped in a {@link SimpleMenuProvider} together with
     * the GUI's title text.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof EssenceExtractorEntity entity) {
                player.openMenu(new SimpleMenuProvider(entity, Component.translatable("block.essentia.essence_extractor")), pos);
            } else {
                // Should never happen: newBlockEntity() always creates an EssenceExtractorEntity
                // for this block, so if it's missing here something has gone very wrong.
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Provides the "ticker" that makes this block's block entity update every game tick
     * (needed for crafting progress to advance over time).
     * <p>
     * Returns {@code null} on the client, since crafting logic only needs to run
     * server-side (the client just displays synced progress data, it doesn't compute it).
     * {@code createTickerHelper} is a {@link BaseEntityBlock} utility that safely checks
     * the requested block entity {@code type} matches our own before calling the tick
     * function, avoiding unsafe casts.
     */
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(type, EssentiaBlockEntities.ESSENCE_EXTRACTOR_BE.get(), (level1, pos, state, entity) ->
                entity.tick(level1, pos, state));
    }

}