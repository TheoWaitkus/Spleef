package com.nutter.spleef;
import java.util.ArrayList;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
  	public BukkitTask duringGameTask;
	private int startx;
	private int endx;
	private int startz;
	private int endz;
	private int altitude;
  	
	public SpleefGame(Main plugin, CommandSender sender, double priceStart)
	{
		this.plugin = plugin;
		creator = sender;
		joinedList = new ArrayList<Player>();
		price = priceStart;
		pot = 0.0;
		isInProgress = false;
		startTask = new GameStartEvent(this.plugin).runTaskLater(this.plugin, 20 * plugin.getConfig().getInt("start-time"));
		new CountdownEvent(this.plugin, this, plugin.getConfig().getInt("start-time"),false, ChatColor.GOLD + "Teleporting joined players in: ").runTaskTimer(plugin,0, 20);


		FileConfiguration config = plugin.getConfig();

		startx = config.getInt("arena-start.x");
		endx = config.getInt("arena-end.x");
		startz = config.getInt("arena-start.z");
		endz = config.getInt("arena-end.z");
		altitude = config.getInt("altitude");

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


		//place barrier blocks here.

		int y = config.getInt("altitude");
		World world = Bukkit.getWorld(plugin.getConfig().getString("world"));

		//sets floor to snow
		for(int x = startx; x <= endx; x++)
		{
			for(int z = startz; z <= endz; z++)
			{
				world.getBlockAt(x,y,z).setType(Material.SNOW_BLOCK);
			}
		}
		Bukkit.broadcastMessage(ChatColor.GOLD + "Join time has ended. Spleefing starting in: ");
		//runs for each player registered for the game
		for(Player p : joinedList)
		{

			//writes each player's inventory to a file.
			p.closeInventory();
			ObjectWriter.writeInventory(plugin, p);
			ObjectWriter.writeCoords(plugin,p);


			//clears the player inventory, including armor.
			PlayerInventory inv = p.getInventory();

			inv.clear();
			inv.setHelmet(new ItemStack(Material.AIR));
			inv.setChestplate(new ItemStack(Material.AIR));
			inv.setLeggings(new ItemStack(Material.AIR));
			inv.setBoots(new ItemStack(Material.AIR));


			//teleports each player to the center of the arena, 2 blocks off the ground (hopefully enough to prevent clips or anything)
			p.teleport(new Location(world, startx + (double)(endx-startx)/2.0, y  + 2, startz + (double)(endz-startz)/2.0));
		}
		startTask = new GameStartEvent(this.plugin).runTaskLater(this.plugin, 20 * plugin.getConfig().getInt("countdown-time"));
		new CountdownEvent(this.plugin, this, plugin.getConfig().getInt("countdown-time"),true, ChatColor.GOLD + "Game starts in: ").runTaskTimer(plugin,0, 20);
	}

	public void onCountdownEnd()
	{
		Bukkit.broadcastMessage(ChatColor.GOLD + "Game has started! Start spleefing!");

		for(Player p : joinedList){
			p.getInventory().addItem(new ItemStack(Material.DIAMOND_SHOVEL));
		}

		duringGameTask = new DuringGameLoopEvent(plugin, this).runTaskTimer(plugin, 1, 1);
	}

	public void onGameEnd()
	{

		Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "The game is over!");

		for(Player p: joinedList){
			p.getInventory().clear();
			ObjectWriter.restoreCoords(plugin,p);
			ObjectWriter.restoreInventory(plugin,p);
		}
		if(joinedList.size() == 1)
		{
			Bukkit.broadcastMessage(ChatColor.GREEN + "The winner is " + ChatColor.GOLD +  joinedList.get(0).getName() + ChatColor.GREEN +  "! They get the pot of " + ChatColor.GOLD + "$" + pot + ChatColor.GREEN + "!" );
			plugin.economy.depositPlayer(joinedList.get(0),pot);
		}
		else
		{
			Bukkit.broadcastMessage(ChatColor.RED + "Nobody wins, the house keeps the pot! Muah hah ha!");

		}
		duringGameTask.cancel();
		plugin.game = null;
	}

	public void perTickDuringGame()
	{
		for(int i = 0; i< joinedList.size(); i++)
		{
			Player p = joinedList.get(i);
			p.setHealth(20);
			p.setFoodLevel(20);

			if((p.getLocation().getBlockX() > endx || p.getLocation().getBlockX() < startx) || (p.getLocation().getBlockZ() > endz || p.getLocation().getBlockZ() < startz) || (p.getLocation().getBlockY() < (altitude-2) || p.getLocation().getBlockY() > (altitude + 5)))
			{
				Bukkit.broadcastMessage(ChatColor.DARK_GREEN + p.getName() + " has been eliminated! Better luck next time!");
				p.getInventory().clear();
				ObjectWriter.restoreInventory(plugin,p);
				ObjectWriter.restoreCoords(plugin,p);
				joinedList.remove(i);
				i--;
			}
		}

		if(joinedList.size() <= 1){
			this.onGameEnd();
		}
	}

}
