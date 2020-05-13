package com.nutter.spleef;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
// Space alien
public class Main extends JavaPlugin
{
	public SpleefGame game;
	static Economy economy;
	

	@Override
	public void onEnable()
	{
		getLogger().info("Spleef Enabled");
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

			//player/console/other sender wants to create a game.
			if(args[0].equalsIgnoreCase("Create"))
			{

				if(game != null){
					sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
					return true;
				}

				double price;

				try{
					price = Double.parseDouble(args[1]);
				}catch(NullPointerException np){
					price = -1.0;
				}catch(NumberFormatException nf){

					price = -1.0;
				}

				if(price < 0){
					sender.sendMessage(ChatColor.DARK_RED + "Invalid price!");
					sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
					return true;
				}

				//attempt to create a game
				SpleefGame game = new SpleefGame(this, sender, price);

				if(game.price != -1.0){
					sender.sendMessage(ChatColor.GREEN + "Successfully created a game, do \"/spleef join\" to join it.");
					Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + " has created a spleef game! type /spleef join to ready up!");
					return true;
				}else{
					sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
					return true;
				}
			}


			//player wants to join the game.
			if(args[0].equalsIgnoreCase("Join"))
			{

				if(game == null){
					sender.sendMessage(ChatColor.DARK_RED + "There is currently no game, start one with \"/spleef create <Price>\".");
					return true;
				}


				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					if(game != null)
					{
						if(game.joinedList.contains(p.getUniqueId()))
						{
							p.sendMessage(ChatColor.GOLD + "You are already in the game. There are " + ChatColor.DARK_GREEN + game.joinedList.size() + ChatColor.GOLD + " players.");
						}
					}
				}

				//we are going to run some method in Spleefer on the sender cast to a player object that makes them join here.


			}

			//player wants to leave the queue for the game
			if(args[0].equalsIgnoreCase("Leave"))
			{


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
