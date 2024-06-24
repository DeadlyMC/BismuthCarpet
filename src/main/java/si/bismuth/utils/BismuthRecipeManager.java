package si.bismuth.utils;

import com.google.common.collect.Lists;

import net.minecraft.crafting.CraftingManager;
import net.minecraft.crafting.recipe.Ingredient;
import net.minecraft.crafting.recipe.Recipe;
import net.minecraft.crafting.recipe.ShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;

public class BismuthRecipeManager {
	private static final Ingredient PAPER = Ingredient.of(Items.PAPER);
	private static final Ingredient SULPHUR = Ingredient.of(Items.GUNPOWDER);
	private static final Recipe DURATION_1 = new ShapelessRecipe("rocket", makeFirework(1), DefaultedList.of(Ingredient.EMPTY, PAPER, SULPHUR));
	private static final Recipe DURATION_2 = new ShapelessRecipe("rocket", makeFirework(2), DefaultedList.of(Ingredient.EMPTY, PAPER, SULPHUR, SULPHUR));
	private static final Recipe DURATION_3 = new ShapelessRecipe("rocket", makeFirework(3), DefaultedList.of(Ingredient.EMPTY, PAPER, SULPHUR, SULPHUR, SULPHUR));

	private static ItemStack makeFirework(int duration) {
		final NbtCompound durationTag = new NbtCompound();
		final NbtCompound fireworksTag = new NbtCompound();
		durationTag.putByte("Flight", (byte) duration);
		fireworksTag.put("Fireworks", durationTag);
		final ItemStack firework = new ItemStack(Items.FIREWORKS, 3);
		firework.setNbt(fireworksTag);
		return firework;
	}

	public static void init() {
		CraftingManager.register("bismuth:durationone", DURATION_1);
		CraftingManager.register("bismuth:durationtwo", DURATION_2);
		CraftingManager.register("bismuth:durationthree", DURATION_3);
	}

	public static void unlockCustomRecipes(ServerPlayerEntity player) {
		player.unlockRecipes(Lists.newArrayList(DURATION_1, DURATION_2, DURATION_3));
	}
}
