package semele.quinn.cursed_barrels;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import semele.quinn.cursed_barrels.block.CursedBarrelBlock;
import semele.quinn.cursed_barrels.block.CursedBarrelBlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CursedBarrelAccessor {
    private static final Map<BlockPos, CursedBarrelAccessor> ACCESSORS = new HashMap<>();

    public static CursedBarrelAccessor getAccessor(LevelAccessor level, BlockPos pos) {
        CursedBarrelAccessor accessor = ACCESSORS.get(pos);

        if (accessor != null) {
            if (!accessor.isValid()) {
                for (BlockPos position : accessor.positions()) {
                    ACCESSORS.remove(position);
                }
            }
        }

        BlockState targetState = level.getBlockState(pos);
        if (targetState.getBlock() instanceof CursedBarrelBlock) {
            BarrelType type = targetState.getValue(CursedBarrelBlock.TYPE);
            Direction facing = targetState.getValue(BlockStateProperties.FACING);

            if (type == BarrelType.TOP) {
                BlockPos neighbourPos = pos.relative(facing.getOpposite());

                if (
                    level.getBlockEntity(pos) instanceof CursedBarrelBlockEntity topEntity &&
                    level.getBlockEntity(neighbourPos) instanceof CursedBarrelBlockEntity bottomEntity
                ) {
                    accessor = new CursedBarrelAccessor(topEntity, bottomEntity);
                    ACCESSORS.put(pos, accessor);
                    ACCESSORS.put(neighbourPos, accessor);

                    return accessor;
                }
            } else {
                BlockPos neighbourPos = pos.relative(facing);

                if (
                    level.getBlockEntity(neighbourPos) instanceof CursedBarrelBlockEntity topEntity &&
                    level.getBlockEntity(pos) instanceof CursedBarrelBlockEntity bottomEntity
                ) {
                    accessor = new CursedBarrelAccessor(topEntity, bottomEntity);
                    ACCESSORS.put(pos, accessor);
                    ACCESSORS.put(neighbourPos, accessor);

                    return accessor;
                }
            }
        }

        return null;
    }

    private final CursedBarrelBlockEntity top;
    private final CursedBarrelBlockEntity bottom;
    private final CombinedInventory combinedInventory;

    public CursedBarrelAccessor(CursedBarrelBlockEntity top, CursedBarrelBlockEntity bottom) {
        this.top = top;
        this.bottom = bottom;
        combinedInventory = new CombinedInventory(top, bottom);
    }

    private boolean isValid() {
        return !(top.isRemoved() || bottom.isRemoved());
    }

    private List<BlockPos> positions() {
        return List.of(top.getBlockPos(), bottom.getBlockPos());
    }

    public MenuProvider getMenu() {
        return new MenuProvider() {
            @NotNull
            @Override
            public Component getDisplayName() {
                //noinspection DataFlowIssue
                return top.hasCustomName() ? top.getCustomName() : bottom.hasCustomName() ? bottom.getCustomName() : top.getName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                if (top.canOpen(player) && bottom.canOpen(player)) {
                    top.unpackLootTable(inventory.player);
                    bottom.unpackLootTable(inventory.player);

                    return ChestMenu.sixRows(syncId, inventory, combinedInventory);
                }

                return null;
            }
        };
    }

    public int getAnalogOutputSignal() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(combinedInventory);
    }

    public WorldlyContainer getInventory() {
        return combinedInventory;
    }

    public void toggleForceOpen() {
        top.toggleForceOpen();
        bottom.toggleForceOpen();
    }
}
