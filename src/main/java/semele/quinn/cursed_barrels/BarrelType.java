package semele.quinn.cursed_barrels;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BarrelType implements StringRepresentable {
    TOP,
    BOTTOM;

    @NotNull
    @Override
    public String getSerializedName() {
        if (this == TOP) {
            return "top";
        } else {
            return "bottom";
        }
    }
}
