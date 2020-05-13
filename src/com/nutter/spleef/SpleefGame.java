package com.nutter.spleef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpleefGame
{
	public Main plugin;
	public ArrayList<Player> joinedList;
  	public double pot;
  	public double price;
  	
	public SpleefGame(Main plugin, CommandSender sender, double priceStart)
	{
		this.plugin = plugin;
		joinedList = new ArrayList<Player>();
		price = priceStart;
		pot = 0.0;
	}

	public boolean addPlayer(Player p)
	{
		joinedList.add(p);
		incrementPot();
		return true;
	}
	public boolean removePlayer(Player p)
	{
		joinedList.remove(p);
		return true;
	}

	public void incrementPot()
	{
		pot += price;
	}
	
	public boolean setArena(CommandSender sender, String[] args)
	{
		return true;
	}
	
	public boolean setMinPrice(CommandSender sender, String[] args)
	{
		return true;
	}
	
	public boolean setStartTime(CommandSender sender, String[] args)
	{
		return true;
	}
	
	public boolean setCountdownTime(CommandSender sender, String[] args)
	{
		return true;
	}


	private void onGameStart ()
	{
		for(Player p : joinedList)
		{

/*
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
*/


			//put whatever witchcraft you are doing to put serializable inventory into memory in here.


		}

	}

}
