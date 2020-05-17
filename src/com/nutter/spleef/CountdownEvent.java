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
    private String prefix;

    public CountdownEvent(Main main, SpleefGame caller, int toCountdown, boolean showEveryone, String prefix){
        plugin = main;
        game = caller;
        countdown = toCountdown;
        this.showEveryone = showEveryone;
        this.prefix = prefix;
    }
    @Override
    public void run()
    {
        if(countdown <= 0)
        {
            this.cancel();
            for(Player p: game.joinedList){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
            }
        }
        else
        {
            if(showEveryone)
            {
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN + prefix + countdown);
            }
            else
            {
                for(Player p: game.joinedList){
                    p.sendMessage(ChatColor.DARK_GREEN + prefix + countdown);
                }
            }


            for(Player p: game.joinedList){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
            }
        }
        countdown--;
    }

}
