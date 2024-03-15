package semele.quinn.cursed_barrels;

import net.minecraft.core.Direction;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class CombinedInventory extends CompoundContainer implements WorldlyContainer {
    private final int[] SLOTS;
    private final WorldlyContainer first;
    private final WorldlyContainer second;

    public CombinedInventory(WorldlyContainer first, WorldlyContainer second) {
        super(first, second);

        SLOTS = IntStream.range(0, first.getContainerSize() + second.getContainerSize()).toArray();
        this.first = first;
        this.second = second;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction face) {
        return slot < first.getContainerSize() ?
                first.canPlaceItemThroughFace(slot, stack, face) :
                second.canPlaceItemThroughFace(slot, stack, face);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return slot < first.getContainerSize() ?
                first.canTakeItemThroughFace(slot, stack, face) :
                second.canTakeItemThroughFace(slot, stack, face);
    }
}
