// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized helper for checking if a block position is a valid roosting spot for bats.
 * Supports solid ceilings, pointed dripstone (stalactites), hanging lanterns, chains, fences, walls, and leaves.
 */
public class BatRoostHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatRoostHelper.class);

    public static boolean isSuitableRoost(Level level, BlockPos pos, BlockPos above) {
        if (!level.isEmptyBlock(pos)) {
            return false;
        }

        BlockState aboveState = level.getBlockState(above);
        if (aboveState.isAir()) {
            return false;
        }

        // 1. Standard sturdy bottom face (solid blocks, slabs, stairs) or redstone conductor
        if (aboveState.isFaceSturdy(level, above, Direction.DOWN) || aboveState.isRedstoneConductor(level, pos)) {
            return true;
        }

        // 2. Pointed Dripstone (Stalactite pointing down)
        if (aboveState.getBlock() instanceof PointedDripstoneBlock) {
            if (aboveState.hasProperty(PointedDripstoneBlock.TIP_DIRECTION) && aboveState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
                return true;
            }
        }

        // 3. Hanging Lantern
        if (aboveState.getBlock() instanceof LanternBlock) {
            if (aboveState.hasProperty(LanternBlock.HANGING) && aboveState.getValue(LanternBlock.HANGING)) {
                return true;
            }
        }

        // 4. Iron Chains, Fences, Walls, and Leaves
        if (aboveState.is(BlockTags.CHAINS) || 
            aboveState.is(BlockTags.FENCES) || 
            aboveState.is(BlockTags.WALLS) || 
            aboveState.is(BlockTags.LEAVES)) {
            return true;
        }

        return false;
    }
}
