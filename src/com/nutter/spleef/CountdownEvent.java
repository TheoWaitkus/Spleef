package com.nutter.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownEvent extends BukkitRunnable {


    private Main plugin;
    private SpleefGame game;
    private int countdown;

    public CountdownEvent(Main main, SpleefGame caller, int toCountdown){
        plugin = main;
        game = caller;
        countdown = toCountdown;
    }
    @Override
    public void run() {
        if(countdown <= 0){
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + Integer.toString(countdown) + "!");
            this.cancel();
        }
        else
        {
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + Integer.toString(countdown) + "!");
        }
    }

}
