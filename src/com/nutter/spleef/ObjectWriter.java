package com.nutter.spleef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ObjectWriter
{
	
	public static void writeInventory(Main plugin, Player p)
	{
        try 
        { 
            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder()+"\\PlayerInventoryData\\"+p.getUniqueId()+".txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(new SerializableInventory(p.getInventory()));
            objectOut.close();
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
	}
	
	public static void restoreInventory(Main plugin,Player p)
	{
        try 
        { 
            FileInputStream fileIn = new FileInputStream(plugin.getDataFolder()+"\\PlayerInventoryData\\"+p.getUniqueId()+".txt");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            
            SerializableInventory inv = (SerializableInventory) obj;
            
            for(int i=0; i< inv.getInventory().size();i++)//map: inv.getInventory()
            {
            	p.getInventory().setItem(i,ItemStack.deserialize(inv.getInventory().get(i)));
            }
            
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