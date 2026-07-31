package de.fene296.essentia.block.custom;

import de.fene296.essentia.block.EssentiaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class DamagedBuddingPrimordial extends Block {

    public static final int GROWTH_CHANCE = 20;
    public static final Direction[] DIRECTIONS = Direction.values();

    public DamagedBuddingPrimordial(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(random.nextInt(GROWTH_CHANCE) == 0) {
            Direction growDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos growPos = pos.relative(growDirection);
            BlockState relativeState = level.getBlockState(growPos);
            Block nextStage = null;

            if(canClusterGrowAtState(relativeState)) {
                nextStage = EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get();
            } else if(relativeState.is(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get()) && relativeState.getValue(AmethystClusterBlock.FACING) == growDirection) {
                nextStage = EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get();
            } else if(relativeState.is(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get()) && relativeState.getValue(AmethystClusterBlock.FACING) == growDirection) {
                nextStage = EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get();
            } else if(relativeState.is(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get()) && relativeState.getValue(AmethystClusterBlock.FACING) == growDirection) {
                nextStage = EssentiaBlocks.PRIMORDIAL_CLUSTER.get();
            }

            if (nextStage != null) {
                BlockState targetState = (nextStage.defaultBlockState().setValue(AmethystClusterBlock.FACING, growDirection)).setValue(AmethystClusterBlock.WATERLOGGED, relativeState.getFluidState().is(Fluids.WATER));
                level.setBlockAndUpdate(growPos, targetState);
            }
        }
    }

    public static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().isFull();
    }
}
