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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CursedBarrelBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items;

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
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items);
        }
    }

    public boolean isForcedOpen() {
        return false;
    }

    public void recheckOpenCount() {

    }
}
