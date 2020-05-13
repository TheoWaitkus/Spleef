package com.nutter.spleef;

import java.io.Serializable;

import org.bukkit.inventory.Inventory;

public class InventoryManagement implements Serializable
{
	private static final long serialVersionUID = 1L;
	public Inventory inventory;
	
	public InventoryManagement(Inventory inventory)
	{
		this.inventory=inventory;
	}

}
