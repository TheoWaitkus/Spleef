package com.nutter.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PriceAlertEvent extends BukkitRunnable {

    private SpleefGame game;

    public PriceAlertEvent(SpleefGame game){
        this.game = game;
    }

    @Override
    public void run() {
        Bukkit.broadcastMessage(ChatColor.RED + "The price to join the game is " + ChatColor.DARK_RED + "$" + game.price + ChatColor.RED + ". If you do not want to spend " + ChatColor.DARK_RED + "$" + game.price + ChatColor.RED + ", do not do /spleef join!");
    }
}
