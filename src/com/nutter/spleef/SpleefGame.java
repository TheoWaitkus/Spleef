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
  	public int pot;
  	public boolean gameInProgress;
  	public double price;
  	
	public SpleefGame(Main plugin, CommandSender sender, double priceStart)
	{
		this.plugin = plugin;

		//here we try to parse the second argument, what should be price, into a double, and if it does not parse, we return an error and cancel the game.
		// Look at where this constructor is called in Main for the response to a value of -1.

		price = priceStart;


		if(price >= 0.0)
		{
			//game is a-go, all args are valid. -- do whatever needs to be done to this object for a game to be ready.

		}
		else
		{
			//game failed, value -1 is used to report to the class that is creating the game that the game should be treated as non-existent.
			price = -1;
		}
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

	}

}
