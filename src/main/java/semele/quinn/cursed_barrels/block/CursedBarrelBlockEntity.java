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
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import semele.quinn.cursed_barrels.BarrelType;

import java.util.stream.IntStream;

public class CursedBarrelBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private static final int[] SLOTS = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> items;
    private boolean isForcedOpen = false;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
            CursedBarrelBlockEntity.this.tryOpen(isForcedOpen, true);
        }

        @Override
        protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
            CursedBarrelBlockEntity.this.tryClose(isForcedOpen, false);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int i, int j) {
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu menu) {
                if (menu.getContainer() instanceof CompoundContainer container) {
                    return container.contains(CursedBarrelBlockEntity.this);
                }
            }

            return false;
        }
    };

    public CursedBarrelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.barrel");
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return ChestMenu.threeRows(syncId, inventory, this);
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public int @NotNull[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        isForcedOpen = tag.getBoolean("forced_open");

        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("forced_open", isForcedOpen);

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items);
        }
    }

    @Override
    public void startOpen(Player player) {
        if (!isRemoved() && !player.isSpectator()) {
            openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!isRemoved() && !player.isSpectator()) {
            openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    public void recheckOpenCount() {
        if (!isRemoved()) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void tryOpen(boolean isForcedOpen, boolean isOpen) {
        if (isForcedOpen ^ isOpen) {
            BlockState state = getBlockState();

            if (state.getValue(CursedBarrelBlock.TYPE) == BarrelType.TOP) {
                playSound(state, SoundEvents.BARREL_OPEN);
            }

            updateBlockState(state, true);
        }
    }

    private void tryClose(boolean isForcedOpen, boolean isOpen) {
        if (!isForcedOpen && !isOpen) {
            BlockState state = getBlockState();

            if (state.getValue(CursedBarrelBlock.TYPE) == BarrelType.TOP) {
                playSound(state, SoundEvents.BARREL_CLOSE);
            }

            updateBlockState(state, false);
        }
    }

    public void toggleForceOpen() {
        isForcedOpen = !isForcedOpen;

        if (isForcedOpen) {
            tryOpen(isForcedOpen, openersCounter.getOpenerCount() > 0);
        } else {
            tryClose(isForcedOpen, openersCounter.getOpenerCount() > 0);
        }
    }

    private void updateBlockState(BlockState state, boolean open) {
        getLevel().setBlock(this.getBlockPos(), state.setValue(BarrelBlock.OPEN, open), 3);
    }

    private void playSound(BlockState blockState, SoundEvent soundEvent) {
        Vec3i upVector = blockState.getValue(BarrelBlock.FACING).getNormal();

        double x = worldPosition.getX() + 0.5D + (upVector.getX() / 2.0D);
        double y = worldPosition.getY() + 0.5D + (upVector.getY() / 2.0D);
        double z = worldPosition.getZ() + 0.5D + (upVector.getZ() / 2.0D);

        getLevel().playSound(null, x, y, z, soundEvent, SoundSource.BLOCKS, 0.5F, getLevel().random.nextFloat() * 0.1F + 0.9F);
    }
}
