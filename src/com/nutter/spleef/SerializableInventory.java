package com.nutter.spleef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SerializableInventory implements Serializable
{
	private static final long serialVersionUID = 1L;
	List<Map<String,Object>> cereal = new ArrayList<Map<String,Object>>();
	List<Map<String,Object>> armor = new ArrayList<Map<String,Object>>();
	public SerializableInventory(PlayerInventory inv)
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

		//we need to include armor in the file.
		for(ItemStack a: inv.getArmorContents()){
			if(a!=null)
			{
				armor.add(a.serialize());
			}
			else
			{
				armor.add((new ItemStack(Material.AIR)).serialize());
			}
		}
	}
	public List<List<Map<String,Object>>> getInventory()
	{
		List<List<Map<String,Object>>> both = new ArrayList<>();
		both.add(cereal);
		both.add(armor);
		return both;

	}
}
