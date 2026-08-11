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

public class EssenceExtractor extends BaseEntityBlock {

    public static final MapCodec<EssenceExtractor> CODEC = simpleCodec(EssenceExtractor::new);
    public static final VoxelShape SHAPE = Block.box(0,0,0 , 16, 10, 16);

    public EssenceExtractor(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EssenceExtractorEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    //Destroyfunction -> drops EntityInventory
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof EssenceExtractorEntity entity) {
            entity.drops();
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    //Rightclick Function
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof EssenceExtractorEntity entity) {
                player.openMenu(new SimpleMenuProvider(entity, Component.translatable("block.essentia.essence_extractor")), pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(type, EssentiaBlockEntities.ESSENCE_EXTRACTOR_BE.get(), (level1, pos, state, entity) ->
                entity.tick(level1, pos, state));
    }

}
