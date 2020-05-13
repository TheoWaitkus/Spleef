package com.nutter.spleef;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Spleefer
{
	Player p;
	Main plugin;
	boolean isInGame;
	InventoryManagement inventoryManager;

  	
	public Spleefer(Main plugin, Player p)
	{
		this.p = p;
		this.plugin = plugin;
		isInGame=false;
		this.inventoryManager=new InventoryManagement(p.getInventory());
	}
	
	public boolean create(String[] args)
	{
		if(Main.economy.has(Bukkit.getServer().getPlayer(p.getName()),Double.parseDouble(args[1])))
		{
			Main.economy.withdrawPlayer(Bukkit.getServer().getPlayer(p.getName()),Double.parseDouble(args[1]));
			isInGame=true;
			return true;
		}
		return false;
	}
		
	public boolean join(String[] args)
	{
		if(Main.economy.has(Bukkit.getServer().getPlayer(p.getName()),Double.parseDouble(args[1])))
		{
			Main.economy.withdrawPlayer(Bukkit.getServer().getPlayer(p.getName()),Double.parseDouble(args[1]));
			isInGame=true;
			return true;
		}
		return false;
	}
	
	public boolean leave(String[] args)
	{
		//Give back inventory tp back to location
		return true;
	}

	private boolean onStart()
	{
		File file = new File(plugin.getDataFolder() + File.separator + "InventoryTemp" + File.separator + p.getUniqueId() + ".yml");

		if(!file.exists())
		{
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);


		return false;
	}
}
