package com.nutter.spleef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SpleefGame
{
	public Main plugin;
	public ArrayList<Player> joinedList;
	public boolean isInProgress;
  	public double pot;
  	public double price;
  	public CommandSender creator;
  	public BukkitTask startTask;
  	
	public SpleefGame(Main plugin, CommandSender sender, double priceStart)
	{
		this.plugin = plugin;
		creator = sender;
		joinedList = new ArrayList<Player>();
		price = priceStart;
		pot = 0.0;
		isInProgress = false;
		startTask = new GameStartEvent(this.plugin).runTaskLater(this.plugin, 20 * plugin.getConfig().getInt("start-time"));
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
	
	public void saveInventory(Player p)
	{
		ObjectWriter.writeInventory(this.plugin,p);
	}

	public void onGameStart ()
	{

		isInProgress = true;
		FileConfiguration config = plugin.getConfig();
		int startx = config.getInt("arena-start.x");
		int endx = config.getInt("arena-end.x");
		int startz = config.getInt("arena-start.z");
		int endz = config.getInt("arena-end.z");

		if(endx < startx)
		{
			int swap = startx;
			startx = endx;
			endx = swap;
		}
		if(endz < startz)
		{
			int swap = startz;
			startz = endz;
			endz = swap;
		}

		int y = config.getInt("altitude");
		World world = Bukkit.getWorld(plugin.getConfig().getString("world"));
		for(int x = startx; x <= endx; x++)
		{
			for(int z = startz; z <= endz; z++)
			{
				world.getBlockAt(x,y,z).setType(Material.SNOW_BLOCK);
			}
		}

		//runs for each player registered for the game
		for(Player p : joinedList)
		{

			//writes each player's inventory to a file.
			ObjectWriter.writeInventory(plugin, p);

			//teleports each player to the center of the arena, 2 blocks off the ground (hopefully enough to prevent clips or anything)
			p.teleport(new Location(world, startx + (double)(endx-startx)/2.0, y  + 2, startz + (double)(endz-startz)/2.0));
		}

		startTask = new GameStartEvent(this.plugin).runTaskLater(this.plugin, 20 * plugin.getConfig().getInt("countdown-time"));
	}

	public void onCountdownEnd()
	{

		//all players are now able to break blocks.



	}

}
