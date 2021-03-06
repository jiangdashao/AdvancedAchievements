package com.hm.achievement.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import com.hm.achievement.AdvancedAchievements;
import com.hm.achievement.category.MultipleAchievements;

/**
 * Listener class to deal with Crafts achievements.
 * 
 * @author Pyves
 *
 */
public class AchieveCraftListener extends AbstractListener {

	public AchieveCraftListener(AdvancedAchievements plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getWhoClicked() instanceof Player) || event.getAction() == InventoryAction.NOTHING
				|| event.getClick() == ClickType.NUMBER_KEY
						&& event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		if (!shouldIncreaseBeTakenIntoAccountNoPermissions(player)) {
			return;
		}

		MultipleAchievements category = MultipleAchievements.CRAFTS;

		ItemStack item = event.getCurrentItem();
		String craftName = item.getType().name().toLowerCase();
		if (!player.hasPermission(category.toPermName() + '.' + craftName)) {
			return;
		}
		if (plugin.getPluginConfig().isConfigurationSection(category + "." + craftName + ':' + item.getDurability())) {
			craftName += ":" + item.getDurability();
		} else if (!plugin.getPluginConfig().isConfigurationSection(category + "." + craftName)) {
			return;
		}

		int eventAmount = event.getCurrentItem().getAmount();
		if (event.isShiftClick()) {
			int maxAmount = event.getInventory().getMaxStackSize();
			ItemStack[] matrix = event.getInventory().getMatrix();
			for (ItemStack itemStack : matrix) {
				if (itemStack != null && itemStack.getType() != Material.AIR) {
					int itemStackAmount = itemStack.getAmount();
					if (itemStackAmount < maxAmount && itemStackAmount > 0) {
						maxAmount = itemStackAmount;
					}
				}
			}
			eventAmount *= maxAmount;
			eventAmount = Math.min(eventAmount, getInventoryAvailableSpace(player, event.getCurrentItem()));
			if (eventAmount == 0) {
				return;
			}
		}

		updateStatisticAndAwardAchievementsIfAvailable(player, category, craftName, eventAmount);
	}
}
