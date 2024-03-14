package semele.quinn.cursed_barrels;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import semele.quinn.cursed_barrels.block.CursedBarrelBlock;

public class CursedBarrels implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cursed-barrels");

	@Override
	public void onInitialize() {
		ResourceLocation barrelId = new ResourceLocation("cursed-barrels", "barrel");
		Block barrelBlock = new CursedBarrelBlock();

		Registry.register(BuiltInRegistries.BLOCK, barrelId, barrelBlock);
		Registry.register(BuiltInRegistries.ITEM, barrelId, new BlockItem(barrelBlock, new Item.Properties()));
	}
}