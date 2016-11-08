package com.hm.achievement.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.achievement.AdvancedAchievements;

/**
 * Abstract class in charge of factoring out common functionality for commands with more than one argument.
 * 
 * @author Pyves
 */
public abstract class AbstractParsableCommand {

	protected AdvancedAchievements plugin;

	/**
	 * Executes the command issued by the player.
	 * 
	 * @param sender
	 * @param args
	 */
	public void executeCommand(CommandSender sender, String[] args) {

		Player player = null;
		// Retrieve player instance with his name.
		for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
			if (currentPlayer.getName().equalsIgnoreCase(args[2])) {
				player = currentPlayer;
				break;
			}
		}

		// If player not found or is offline.
		if (player == null) {
			sender.sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
					.getString("player-offline", "The player PLAYER is offline!").replace("PLAYER", args[2]));
			return;
		}

		executeSpecificActions(sender, args, player);
	}

	/**
	 * Extracts the name of the achievement from the command line arguments.
	 * 
	 * @param args
	 * @return
	 */
	protected String parseAchievementName(String[] args) {

		StringBuilder achievementName = new StringBuilder();
		// Rebuild name of achievement by concatenating elements in the string array. The name of the player is last.
		for (int i = 1; i < args.length - 1; i++) {
			achievementName.append(args[i]);
			if (i != args.length - 2)
				achievementName.append(' ');
		}
		return achievementName.toString();
	}

	/**
	 * Executes actions specific to the class extending this abstract class.
	 * 
	 * @param sender
	 * @param args
	 * @param player
	 */
	protected abstract void executeSpecificActions(CommandSender sender, String[] args, Player player);

}
