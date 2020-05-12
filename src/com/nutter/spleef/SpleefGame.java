package com.nutter.spleef;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpleefGame
{
	Main plugin;
  	String[] joinedList;
  	static int pot;
  	static boolean gameInProgress;
  	int minPrice;
  	
	public SpleefGame(Main plugin)
	{
		this.plugin=plugin;
		
		minPrice=0;
	}
	
	public void incrementPot(int increment)
	{
		pot+=increment;
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

  private boolean perPlayerOnStart(Player p)
  {
    File file = new File(plugin.getDataFolder() + File.separator + "InventoryTemp" + File.separator + p.getUniqueId() + ".yml");

    if(!file.exists())
    {
      file.createNewFile();
    }
    YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);

  }

  private void onGameStart ()
  {

  }

}
