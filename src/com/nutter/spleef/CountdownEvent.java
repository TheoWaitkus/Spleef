package com.nutter.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownEvent extends BukkitRunnable {


    private Main plugin;
    private SpleefGame game;
    private int countdown;
    private boolean showEveryone;

    public CountdownEvent(Main main, SpleefGame caller, int toCountdown, boolean showEveryone){
        plugin = main;
        game = caller;
        countdown = toCountdown;
        this.showEveryone = showEveryone;

    }
    @Override
    public void run()
    {
        if(countdown <= 0)
        {
            this.cancel();
            for(Player p: game.joinedList){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 15, 3);
            }
        }
        else
        {
            if(showEveryone)
            {
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN + Integer.toString(countdown));
                for(Player p: game.joinedList){
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 15, 1);
                }
            }
            else
            {
                for(Player p: game.joinedList){
                    p.sendMessage(ChatColor.DARK_GREEN + Integer.toString(countdown));
                }
            }
        }
        countdown--;
    }

}
