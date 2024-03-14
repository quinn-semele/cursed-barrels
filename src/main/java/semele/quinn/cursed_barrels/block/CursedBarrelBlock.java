/*
 * Copyright 2024 Quinn Semele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package semele.quinn.cursed_barrels.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import semele.quinn.cursed_barrels.BarrelType;
import semele.quinn.cursed_barrels.CursedBarrels;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class CursedBarrelBlock extends Block {
    private static final EnumProperty<BarrelType> TYPE = EnumProperty.create("type", BarrelType.class);

    public CursedBarrelBlock() {
        super(Properties.ofFullCopy(Blocks.BARREL));

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(TYPE, BarrelType.BOTTOM)
                        .setValue(FACING, Direction.UP)
                        .setValue(OPEN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, OPEN, FACING);
    }


    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BarrelType type = state.getValue(TYPE);
        Direction facing = state.getValue(FACING);
        Direction offset = type.asOffset(state.getValue(FACING));

        if (!isValidCursedBarrel(state, level.getBlockState(pos.relative(offset)))) {
            return Blocks.BARREL.defaultBlockState().setValue(FACING, facing).setValue(OPEN, state.getValue(OPEN));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public static BlockState getPlacementState(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (context.isSecondaryUseActive()) {
            Direction clickedDirection = context.getClickedFace().getOpposite();
            BlockState clickedState = level.getBlockState(pos.relative(clickedDirection));

            if (isValidMergeTarget(clickedState, clickedDirection)) {
                Direction barrelFacing = clickedState.getValue(FACING);
                BarrelType type = getBarrelTypeForNeighbour(barrelFacing, clickedDirection);

                if (type != null) {
                    return CursedBarrels.BLOCK.defaultBlockState().setValue(FACING, barrelFacing).setValue(TYPE, type);
                }
            }
        } else {
            Direction barrelFacing = context.getNearestLookingDirection().getOpposite();
            List<Direction> options = List.of(barrelFacing.getOpposite(), barrelFacing);

            for (Direction option : options) {
                BlockState clickedState = level.getBlockState(pos.relative(option));

                if (isValidMergeTarget(clickedState, option)) {
                    BarrelType type = getBarrelTypeForNeighbour(barrelFacing, option);

                    if (type != null) {
                        return CursedBarrels.BLOCK.defaultBlockState().setValue(FACING, barrelFacing).setValue(TYPE, type);
                    }
                }
            }
        }

        return null;
    }

    public static BlockState getNeighbourUpdateState(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (neighborState.is(CursedBarrels.BLOCK)) {
            Direction barrelFacing = state.getValue(FACING);

            if (pos.relative(barrelFacing).equals(neighborPos) || neighborPos.relative(barrelFacing).equals(pos)) {
                BlockState newState = CursedBarrels.BLOCK.defaultBlockState().setValue(FACING, barrelFacing).setValue(TYPE, getBarrelTypeForNeighbour(barrelFacing, direction));

                if (isValidCursedBarrel(newState, neighborState)) {
                    return newState;
                }
            }
        }

        return state;
    }

    public static BarrelType getBarrelTypeForNeighbour(Direction barrelFacing, Direction offset) {
        return barrelFacing == offset.getOpposite() ? BarrelType.TOP : BarrelType.BOTTOM;
    }

    public static boolean isValidMergeTarget(BlockState clickedState, Direction clickedDirection) {
        if (!clickedState.is(Blocks.BARREL)) {
            return false;
        }

        Direction facing = clickedState.getValue(FACING);
        return facing == clickedDirection || facing == clickedDirection.getOpposite();
    }

    public static boolean isValidCursedBarrel(BlockState barrel, BlockState otherState) {
        return otherState.is(barrel.getBlock())
                && barrel.getValue(FACING) == otherState.getValue(FACING)
                && barrel.getValue(TYPE).getOpposite() == otherState.getValue(TYPE);
    }
}
