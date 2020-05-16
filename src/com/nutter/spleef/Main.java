package com.nutter.spleef;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

/* TODO
-Write an onPlayerJoin listener that checks if players have a file written with their inventory and gives it to them, before deleting it.

-Make the config store arena location, start time, and countdown time properly, and have any changes made to those be changed in the config as well.

-Implement SerializableInventory in SpleefGame.onGameStart, to be created and then serialized and saved on the server disk in the plugin.getDataFolder general area.

-Write the rest of onGameStart, including, but not limited to:
	-Replace all snow in the arena
	-Teleport all participating players, stored in game.joinedList, to the center of the arena
	-Save player inventory
	-Clear player inventory

-Create a function that checks each players position every tick, to tell if they have fell or not.

-Make sure players cannot use any command that teleports them while they are in a game, without being disqualified (this could be solved by checking if they are within the arena every tick, and if not, disqualifying them).

-Figure out how to tell if the game is over, perhaps removing players from joinedList after they call?

 */

public class Main extends JavaPlugin
{
	
	public SpleefGame game;
	static Economy economy;

	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		setupEconomy();
		getLogger().info("Spleef Enabled");
		setupEconomy();
		game = null;
		for (Player p: Bukkit.getOnlinePlayers())
		{
			ObjectWriter.writeInventory(this,p);
			p.getInventory().clear();
			ObjectWriter.restoreInventory(this,p);
		}
	}

	@Override
	public void onDisable()
	{
		getLogger().info("Spleef Disabled");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(label.equalsIgnoreCase("Spleef"))
		{
			FileConfiguration config = this.getConfig();

			if(args.length > 0) {
				if (args[0].equalsIgnoreCase("Create") && config.isSet("world") && config.isSet("arena-start.x") && config.isSet("arena-start.z") && config.isSet("arena-end.x") && config.isSet("arena-end.z") && config.isSet("altitude")) {

					//player/console/other sender wants to create a game.
					if (game != null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}

					double price;


					//testing if the price can parse properly, and if not, using -1.0 as a tag that something went wrong.
					try {
						price = Double.parseDouble(args[1]);
					} catch (NullPointerException np) {

						price = -1.0;

					} catch (NumberFormatException nf) {

						price = -1.0;
					}


					//alerts the sender that the game is invalidated, and does not create a game.
					if (price < 0) {
						sender.sendMessage(ChatColor.DARK_RED + "Invalid price!");
						sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
						return true;
					}


					SpleefGame game = new SpleefGame(this, sender, price);
					sender.sendMessage(ChatColor.DARK_GREEN + "Successfully created a game, do \"/spleef join\" to join it.");
					Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + " has created a spleef game! type /spleef join to ready up!");
					return true;

				} else {
					sender.sendMessage("Arena area has not been defined yet, ask an admin to set it up.");
				}


				//player wants to join the game.
				if (args[0].equalsIgnoreCase("Join")) {

					if (game == null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is currently no game, start one with \"/spleef create <Price>\".");
						return true;
					}
					if (game.isInProgress) {
						sender.sendMessage(ChatColor.DARK_RED + "Game is currently in progress, please wait until after it is complete.");
						return true;
					}
					if (sender instanceof Player) {
						Player p = (Player) sender;

						if (game.joinedList.contains(p)) {
							p.sendMessage(ChatColor.DARK_RED + "You are already in the game.");
							return true;
						} else {
							if (economy.has(p, game.price)) {
								economy.withdrawPlayer(p, game.price);
								Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " has joined the spleef game! There are " + ChatColor.DARK_GREEN + game.joinedList.size() + ChatColor.GOLD + " players.");
								return game.addPlayer(p);
							}

						}
					}
					sender.sendMessage(ChatColor.DARK_RED + "You are not a player, ya dingus! You can't join a game!");
					return true;
				}

				//player wants to leave the queue for the game
				if (args[0].equalsIgnoreCase("Leave")) {
					if (game == null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is currently no game.");
						return true;
					}
					if (game.isInProgress) {
						sender.sendMessage(ChatColor.DARK_RED + "Game is currently in progress, please wait until after it is complete.");
						return true;
					}
					if (sender instanceof Player) {

						Player p = (Player) sender;
						if (game.joinedList.contains(p)) {
							p.sendMessage(ChatColor.DARK_GREEN + "You have left the game.");
							return game.removePlayer(p);
						} else {

						}
					}
					return false;
				}

				//player/console/other sender is attempting to setup an area to be the spleef arena.
				if (args[0].equalsIgnoreCase("SetArena")) {

					if (sender.hasPermission("spleef.admin")) {
						if (game != null) {
							sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
							return true;
						}

						if (args.length == 1) {
							sender.sendMessage("Usage: /spleef setarena <world> <start x> <start z> <end x> <end z> <altitude>");
							return true;
						}
						if (args.length == 7) {
							int[] coords = new int[5];
							for (int i = 2; i < 7; i++) {
								try {
									coords[i - 2] = Integer.parseInt(args[i]);
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
									return onCommand(sender, command, label, new String[]{"setarena"});

								}

							}
							if (Bukkit.getServer().getWorld(args[0]) == null) {
								sender.sendMessage("That world does not exist!");
								return true;
							}
							config.set("world", args[1]);
							config.set("arena-start.x", coords[0]);
							config.set("arena-start.z", coords[1]);
							config.set("arena-end.x", coords[2]);
							config.set("arena-end.z", coords[3]);
							config.set("altitude", coords[4]);
							this.saveConfig();
							sender.sendMessage(ChatColor.GOLD + Integer.toString(Math.abs(coords[0] - coords[2])) + "*" + Integer.toString(Math.abs(coords[1] - coords[3])) + " Arena defined.");

							return true;

						}
						sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
						return onCommand(sender, command, label, new String[]{"setarena"});
					}


				}

				//player/console/other sender is attempting to set the minimum price for joining a game.
				if (args[0].equalsIgnoreCase("SetMinPrice")) {
					if (sender.hasPermission("spleef.admin")) {
						if (game != null) {
							sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
							return true;
						}

						if (args.length == 2) {
							try {
								Integer.parseInt(args[1]);
								config.set("min-price", args[1]);
								sender.sendMessage(ChatColor.GOLD + "MinPrice set");
								return true;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
								return true;
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
						return true;
					}
				}

				//player/console/other sender is attempting to set the time it takes for the game to start after it is created.
				if (args[0].equalsIgnoreCase("SetStartTime")) {
					if (sender.hasPermission("spleef.admin")) {
						if (game != null) {
							sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
							return true;
						}
						if (args.length == 2) {
							try {
								Integer.parseInt(args[1]);
								config.set("start-time", args[1]);
								sender.sendMessage(ChatColor.GOLD + "StartTime set");
								return true;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
								return true;
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
						return true;
					}
				}

				//player/console/other sender is attempting to set the time it takes for the snow blocks to be able to be broken after players get teleported.
				if (args[0].equalsIgnoreCase("SetCountdownTime")) {
					if (sender.hasPermission("spleef.admin")) {
						if (game != null) {
							sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
							return true;
						}
						if (args.length == 2) {
							try {
								Integer.parseInt(args[1]);
								config.set("countdown-time", args[1]);
								sender.sendMessage(ChatColor.GOLD + "CountdownTime set");
								return true;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
								return true;
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
						return true;
					}
				}
			}
			sender.sendMessage("avaliable commands:");
			sender.sendMessage("create, join");
			if(sender.hasPermission("spleef.admin")){
				sender.sendMessage("admin commands:");
				sender.sendMessage("setarena, setminprice, setstarttime, setcountdowntime");
			}
			return true;
		}
		return false;
	}
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
