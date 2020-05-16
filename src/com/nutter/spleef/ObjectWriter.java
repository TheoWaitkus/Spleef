package com.nutter.spleef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ObjectWriter
{
	
	public static void writeInventory(Main plugin, Player p)
	{
        try 
        { 
            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder()+File.separator+"PlayerInventoryData"+File.separator+p.getUniqueId());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(new SerializableInventory(p.getInventory()));
            objectOut.close();
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
	}
	
	/*public static Inventory readInventory(Player p)
	{
		
	}*/
}