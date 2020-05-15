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

-Make the config store arena location, start time, and countdown time properly, and have any changes made to those be changed in the config as well.

-Implement SerializableInventory in SpleefGame.onGameStart, to be created and then serialized and saved on the server disk in the plugin.getDataFolder general area.

-Write the rest of onGameStart, including, but not limited to:
	-Replace all snow in the arena
	-Teleport all participating players, stored in game.joinedList, to the center of the arena



 */

public class Main extends JavaPlugin
{
	
	public SpleefGame game;
	static Economy economy;
	static FileConfiguration config;

	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		setupEconomy();
		getLogger().info("Spleef Enabled");
		setupEconomy();
		game = null;



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
			if(!(game.isInProgress))
			{
				//player/console/other sender wants to create a game.
				if(args[0].equalsIgnoreCase("Create"))
				{

					if(game != null)
					{
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}

					double price;



					//testing if the price can parse properly, and if not, using -1.0 as a tag that something went wrong.
					try{
						price = Double.parseDouble(args[1]);
					}catch(NullPointerException np){

						price = -1.0;

					}catch(NumberFormatException nf){

						price = -1.0;
					}


					//alerts the sender that the game is invalidated, and does not create a game.
					if(price < 0){
						sender.sendMessage(ChatColor.DARK_RED + "Invalid price!");
						sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
						return true;
					}


					SpleefGame game = new SpleefGame(this, sender, price);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Successfully created a game, do \"/spleef join\" to join it.");
                    Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + " has created a spleef game! type /spleef join to ready up!");
                    return true;

				}


				//player wants to join the game.
				if(args[0].equalsIgnoreCase("Join"))
				{

					if(game == null)
					{
						sender.sendMessage(ChatColor.DARK_RED + "There is currently no game, start one with \"/spleef create <Price>\".");
						return true;
					}
					if(sender instanceof Player)
					{
						Player p = (Player) sender;

						if(game.joinedList.contains(p))
						{
							p.sendMessage(ChatColor.DARK_RED + "You are already in the game.");
							return true;
						}
						else
						{
							if (economy.has(p, game.price))
							{
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
				if(args[0].equalsIgnoreCase("Leave"))
				{
					if(game == null)
					{
						sender.sendMessage(ChatColor.DARK_RED + "There is currently no game.");
						return true;
					}
					if(sender instanceof Player)
					{

						Player p = (Player) sender;
						if(game.joinedList.contains(p))
						{
							p.sendMessage(ChatColor.DARK_GREEN + "You have left the game.");
							return game.removePlayer(p);
						}
						else
						{

						}
					}
					return false;
				}

				//player/console/other sender is attempting to setup an area to be the spleef arena.
				if(args[0].equalsIgnoreCase("SetArena"))
				{
					return game.setArena(sender,args);
				}

				//player/console/other sender is attempting to set the minimum price for joining a game.
				if(args[0].equalsIgnoreCase("SetMinPrice"))
				{
					return game.setMinPrice(sender,args);
				}

				//player/console/other sender is attempting to set the time it takes for the game to start after it is created.
				if(args[0].equalsIgnoreCase("SetStartTime"))
				{
					return game.setStartTime(sender,args);
				}

				//player/console/other sender is attempting to set the time it takes for the snow blocks to be able to be broken after players get teleported.
				if(args[0].equalsIgnoreCase("SetCountdownTime"))
				{
					return game.setCountdownTime(sender,args);
				}
			}
			sender.sendMessage(ChatColor.DARK_RED + "Game is currently in progress, please wait until after it is complete.");
			return true;
		}
		return false;
	}
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
