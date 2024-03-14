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

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import semele.quinn.cursed_barrels.block.CursedBarrelBlock;

public class CursedBarrels implements ModInitializer {
	public static final CursedBarrelBlock BLOCK = new CursedBarrelBlock();

	@Override
	public void onInitialize() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("cursed-barrels", "barrel"), BLOCK);
	}
}