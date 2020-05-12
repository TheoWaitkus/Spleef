package com.nutter.spleef;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spleefer
{
	Main plugin;
	boolean isInGame;

  	
	public Spleefer(Main plugin)
	{
		this.plugin=plugin;
		isInGame=false;
	}
	
	public boolean create(CommandSender sender, String[] args)
	{
		if(Main.economy.has(Bukkit.getServer().getPlayer(sender.getName()),Double.parseDouble(args[1])))
		{
			Main.economy.withdrawPlayer(Bukkit.getServer().getPlayer(sender.getName()),Double.parseDouble(args[1]));
			isInGame=true;
			return true;
		}
		return false;
	}
	
	public boolean join(CommandSender sender, String[] args)
	{
		if(Main.economy.has(Bukkit.getServer().getPlayer(sender.getName()),Double.parseDouble(args[1])))
		{
			Main.economy.withdrawPlayer(Bukkit.getServer().getPlayer(sender.getName()),Double.parseDouble(args[1]));
			isInGame=true;
			return true;
		}
		return false;
	}
	
	public boolean leave(CommandSender sender, String[] args)
	{
		//Give back inventory tp back to location
		return true;
	}
}
