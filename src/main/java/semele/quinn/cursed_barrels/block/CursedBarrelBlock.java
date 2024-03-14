package semele.quinn.cursed_barrels.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import semele.quinn.cursed_barrels.BarrelType;

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
}
