package com.nutter.spleef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SerializableInventory implements Serializable
{
	private static final long serialVersionUID = 1L;
	List<Map<String,Object>> cereal = new ArrayList<Map<String,Object>>();
	public SerializableInventory(Inventory inv)
	{
		for(ItemStack i: inv.getContents())
		{
			if(i!=null)
			{
				cereal.add(i.serialize());
			}
			else
			{
				cereal.add((new ItemStack(Material.AIR)).serialize());
			}
		}
	}
	public List<Map<String,Object>> getInventory()
	{
		return cereal;
	}
}
