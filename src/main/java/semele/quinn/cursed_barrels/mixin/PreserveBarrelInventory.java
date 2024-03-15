package semele.quinn.cursed_barrels.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import semele.quinn.cursed_barrels.CursedBarrels;
import semele.quinn.cursed_barrels.block.CursedBarrelBlockEntity;

@Mixin(LevelChunk.class)
public abstract class PreserveBarrelInventory {
    @Unique
    private RandomizableContainerBlockEntity cursed_barrels$inventory;

    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos pos, LevelChunk.EntityCreationType creationType);

    @Inject(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void cursed_barrels$captureOldBarrelInventory(
            BlockPos pos,
            BlockState newState,
            boolean moved,
            CallbackInfoReturnable<BlockState> cir,
            int posY,
            LevelChunkSection levelChunkSection,
            boolean isChunkOnlyAir,
            int localX,
            int localY,
            int localZ,
            BlockState state,
            Block newBlock
    ) {
        if (state.hasBlockEntity()) {
            if (state.is(CursedBarrels.BLOCK) && newState.is(Blocks.BARREL)) {
                if (getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK) instanceof CursedBarrelBlockEntity entity) {
                    cursed_barrels$inventory = entity;
                }
            } else if (state.is(Blocks.BARREL) && newState.is(CursedBarrels.BLOCK)) {
                if (getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK) instanceof BarrelBlockEntity entity) {
                    cursed_barrels$inventory = entity;
                }
            }
        }
    }

    @Inject(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/chunk/LevelChunk;unsaved:Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void cursed_barrels$preserveBarrelInventory(
            BlockPos pos,
            BlockState newState,
            boolean moved,
            CallbackInfoReturnable<BlockState> cir,
            int posY,
            LevelChunkSection levelChunkSection,
            boolean isChunkOnlyAir,
            int localX,
            int localY,
            int localZ,
            BlockState state
    ) {
        if (state.is(CursedBarrels.BLOCK) && newState.is(Blocks.BARREL)) {
            if (cursed_barrels$inventory != null) {
                if (getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK) instanceof BarrelBlockEntity entity) {
                    entity.setItems(cursed_barrels$inventory.getItems());
                }
            }
        } else if (state.is(Blocks.BARREL) && newState.is(CursedBarrels.BLOCK)) {
            if (cursed_barrels$inventory != null) {
                if (getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK) instanceof CursedBarrelBlockEntity entity) {
                    entity.setItems(cursed_barrels$inventory.getItems());
                }
            }
        }

        cursed_barrels$inventory = null;
    }
}
