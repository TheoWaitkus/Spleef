package com.nutter.spleef;

import org.bukkit.scheduler.BukkitRunnable;

public class DuringGameLoopEvent extends BukkitRunnable {

    private Main plugin;
    private SpleefGame game;

    public DuringGameLoopEvent(Main main, SpleefGame caller){
        plugin = main;
        game = caller;
    }
    @Override
    public void run() 
    {
        game.perTickDuringGame();
    }
}
