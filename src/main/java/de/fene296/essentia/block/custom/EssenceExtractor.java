package de.fene296.essentia.block.custom;

import com.mojang.serialization.MapCodec;
import de.fene296.essentia.block.entity.EssenceExtractorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class EssenceExtractor extends BaseEntityBlock {

    public static final VoxelShape SHAPE = Block.box(0,0,0 , 16, 10, 16);
    public static final MapCodec<EssenceExtractor> CODEC = simpleCodec(EssenceExtractor::new);

    public EssenceExtractor(Properties properties) {
        super(properties);
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // Block Entity

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EssenceExtractorEntity(blockPos, blockState);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if(level.getBlockEntity(pos) instanceof EssenceExtractorEntity essenceExtractorEntity) {
            essenceExtractorEntity.drops();
            level.updateNeighbourForOutputSignal(pos, this);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof EssenceExtractorEntity essenceExtractorEntity) {
            if(player.isCrouching()) {
                player.openMenu(new SimpleMenuProvider(essenceExtractorEntity, Component.translatable("block.essentia.essence_extractor")), pos);
                return InteractionResult.SUCCESS;
            }

            boolean isPedestalEmpty = essenceExtractorEntity.inventory.getResource(0).isEmpty();

            // Insert
            if(isPedestalEmpty && !itemStack.isEmpty()) {
                essenceExtractorEntity.inventory.set(0, ItemResource.of(itemStack), 1);
                itemStack.shrink(1);
                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            }
            // Extract
            else if(!isPedestalEmpty) {
                ItemStack stackOnPedestal = essenceExtractorEntity.inventory.getResource(0).toStack();
                essenceExtractorEntity.clearContents();

                if(!player.getInventory().add(stackOnPedestal)) {
                    player.drop(stackOnPedestal, false);
                }

                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
