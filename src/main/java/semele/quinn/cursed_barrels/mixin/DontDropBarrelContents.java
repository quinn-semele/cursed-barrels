package semele.quinn.cursed_barrels.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import semele.quinn.cursed_barrels.CursedBarrels;

@Mixin(BarrelBlock.class)
public abstract class DontDropBarrelContents extends BlockBehaviour {
    protected DontDropBarrelContents(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "onRemove(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cursed_barrels$dontDropContents(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean moved,
            CallbackInfo ci
    ) {
        if (state.is(Blocks.BARREL) && newState.is(CursedBarrels.BLOCK)) {
            //noinspection deprecation
            super.onRemove(state, level, pos, newState, moved);
            ci.cancel();
        }
    }
}
