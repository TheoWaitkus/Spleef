package com.nutter.spleef;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartEvent extends BukkitRunnable
{

    private Main plugin;

    public GameStartEvent(Main main)
    {
        this.plugin = main;
    }

    @Override
    public void run()
    {
        // What you want to schedule goes here
        if(plugin.game != null)
        {
            if(plugin.game.isInProgress)
            {
                plugin.game.onCountdownEnd();
            }
            else
            {
                plugin.game.onGameStart();
            }

        }
    }

}
