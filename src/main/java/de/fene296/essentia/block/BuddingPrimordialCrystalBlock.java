package de.fene296.essentia.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BuddingPrimordialCrystalBlock extends Block {

    private static final int GROWTH_CHANCE = 5;

    public BuddingPrimordialCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(random.nextInt(GROWTH_CHANCE) != 0) {
            return;
        }

        Direction direction = Direction.getRandom(random);
        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);

        Block nextStage = getNextStage(targetState, direction);
        if(nextStage == null) {
            return;
        }

        BlockState newState = nextStage.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);

        level.setBlockAndUpdate(targetPos, newState);

    }

    private static Block getNextStage(BlockState targetState, Direction direction) {
        if (canClusterGrowAtState(targetState)) {
            return EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get();
        }
        if (targetState.is(EssentiaBlocks.SMALL_PRIMORDIAL_BUD.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            return EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get();
        }

        if (targetState.is(EssentiaBlocks.MEDIUM_PRIMORDIAL_BUD.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            return EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get();
        }

        if (targetState.is(EssentiaBlocks.LARGE_PRIMORDIAL_BUD.get()) && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            return EssentiaBlocks.PRIMORDIAL_CRYSTAL_CLUSTER.get();
        }
        return null;
    }

    private static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }
}
