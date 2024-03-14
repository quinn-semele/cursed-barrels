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

package semele.quinn.cursed_barrels;

import net.minecraft.core.Direction;
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

    public BarrelType getOpposite() {
        if (this == TOP) {
            return BOTTOM;
        } else {
            return TOP;
        }
    }

    public Direction asOffset(Direction facing) {
        return this == TOP ? facing.getOpposite() : facing;
    }
}
