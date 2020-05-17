package com.nutter.spleef;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpleefListener implements Listener
{
	Main plugin;
	public SpleefListener(Main plugin)
	{
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		Entity projectile = event.getEntity();
		Block block = event.getHitBlock();
		if(block!=null && projectile.getType()==EntityType.SNOWBALL && block.getType()==Material.SNOW_BLOCK)
		{
			
			FileConfiguration config=plugin.getConfig();
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
	        plugin.getLogger().info(Integer.toString(startx));
			if(block.getX()>=(double)startx && block.getX()<=(double)endx)
			{
				if(block.getZ()>=(double)startz && block.getZ()<=(double)endz)
				{
					
					block.breakNaturally();
					projectile.remove();
				}
			}

		}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		File inventory = new File(plugin.getDataFolder()+ File.separator +"PlayerInventoryData"+ File.separator +p.getUniqueId()+".txt");
		File location = new File(plugin.getDataFolder()+ File.separator +"PlayerCoordData"+ File.separator +p.getUniqueId()+".txt");
		if(inventory.exists())
		{
			p.getInventory().clear();
			ObjectWriter.restoreInventory(plugin,p);
		}
		if(location.exists())
		{
			ObjectWriter.restoreCoords(plugin,p);
		}
	}
	
}
