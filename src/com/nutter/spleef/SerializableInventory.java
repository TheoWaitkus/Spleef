package com.nutter.spleef;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SerializableInventory implements Serializable
{
	private static final long serialVersionUID = 1L;
	ArrayList<ItemStack> cereal;
	public SerializableInventory(Inventory inv)
	{
		for (ItemStack i : inv)
		{
			cereal.add(i.clone());
		}
	}
	public ArrayList<ItemStack> getInventory()
	{
		return cereal;
	}
}
