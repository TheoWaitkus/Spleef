package com.nutter.spleef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class ObjectWriter
{
	
	public static void writeInventory(Main plugin, Player p)
	{
        try 
        { 
            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder()+"\\PlayerInventoryData\\"+p.getUniqueId()+".txt");
            BukkitObjectOutputStream objectOut = new BukkitObjectOutputStream(new ObjectOutputStream(fileOut));
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
            BukkitObjectInputStream objectIn = new BukkitObjectInputStream(new ObjectInputStream(fileIn));
            Object obj = objectIn.readObject();
            objectIn.close();
            
            SerializableInventory inv = (SerializableInventory) obj;

            //both is a list of both main inventory and armor from the player
            List<List<Map<String,Object>>> both = inv.getInventory();

            for(int i=0; i< both.get(0).size();i++)//map: inv.getInventory()
            {
            	p.getInventory().setItem(i,ItemStack.deserialize(both.get(0).get(i)));
            }

            //assigning armorContents to specifically the armor portion of both
            List<Map<String,Object>> armorContents = both.get(1);

            //making it into an arraylist, because that is what setArmorContents takes.
            ItemStack[] toArmor = new ItemStack[armorContents.size()];
            
            for(int i=0; i<armorContents.size();i++)
            {
                toArmor[i] = ItemStack.deserialize(armorContents.get(i));

            }
            p.getInventory().setArmorContents(toArmor);


        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
	}
	
	public static void writeCoords(Main plugin, Player p)
	{
        try 
        { 
            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder()+"\\PlayerCoordData\\"+p.getUniqueId()+".txt");
            ObjectOutputStream objectOut = new ObjectOutputStream(new ObjectOutputStream(fileOut));
            objectOut.writeObject(p.getLocation().serialize());
            objectOut.close();
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }
	}
	
	public static void restoreCoords(Main plugin,Player p)
	{
        try 
        { 
            FileInputStream fileIn = new FileInputStream(plugin.getDataFolder()+"\\PlayerCoordData\\"+p.getUniqueId()+".txt");
            ObjectInputStream objectIn = new ObjectInputStream(new ObjectInputStream(fileIn));
            Object obj = objectIn.readObject();
            objectIn.close();
            
            Location location = Location.deserialize(((List<Map<String,Object>>) obj).get(0));
			p.teleport(location);
			
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