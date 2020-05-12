package com.nutter.spleef;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Main extends JavaPlugin
{
	public SpleefGame game;
	static Economy economy;
	

	@Override
	public void onEnable()
	{
		getLogger().info("Spleef Enabled");
		game = new SpleefGame(this);
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
			if(args[0].equalsIgnoreCase("Create"))
			{
				return game.create(sender, args);
			}
			if(args[0].equalsIgnoreCase("Join"))
			{
				return game.join(sender, args);
			}
			if(args[0].equalsIgnoreCase("Leave"))
			{
				return game.join(sender, args);
			}
			if(args[0].equalsIgnoreCase("SetArena"))
			{
				return game.setArena(sender,args);
			}
			if(args[0].equalsIgnoreCase("SetMinPrice"))
			{
				return game.setMinPrice(sender,args);
			}
			if(args[0].equalsIgnoreCase("SetStartTime"))
			{
				return game.setStartTime(sender,args);
			}
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
