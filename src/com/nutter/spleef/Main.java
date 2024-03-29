package com.nutter.spleef;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/*
    FIXME

	-make it so that when you die after the game starts, you don't lose your entire inventory!

	-make it so that it automatically creates folders and files where it needs them.

	TODO

	-make players spawn randomly instead of in one central location.

	-make the items in the arena disappear at the end.

	-add multiple layers (give # of layers when doing /create, so like... /create <price> <#layers>) up to a max of 3

	-add power-ups that spawn randomly around the arena, that you use by either right clicking or just picking up.

		power-up ideas:

			-item that makes all snow under you obsidian for a few seconds.




 */

public class Main extends JavaPlugin
{
	
	public SpleefGame game;
	public static Economy economy;

	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		SpleefListener listener = new SpleefListener(this);
		pm.registerEvents(listener, this);
		setupEconomy();
		getLogger().info("Spleef Enabled");
		setupEconomy();
		game = null;
	}

	@Override
	public void onDisable()
	{
		getLogger().info("Spleef Disabled");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(label.equalsIgnoreCase("Spleef"))
		{
			FileConfiguration config = this.getConfig();

			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "avaliable commands:");
				sender.sendMessage("create, join, leave");
				if(sender.hasPermission("spleef.admin")){
					sender.sendMessage(ChatColor.RED + "admin commands:");
					sender.sendMessage("setarena, setminprice, setstarttime, setcountdowntime");
				}
				return true;
			}
			//player wants to create a game.
			if(args[0].equalsIgnoreCase("Create"))
			{
				if (args.length == 1) {
					sender.sendMessage("Usage: /spleef create <priceToJoin>");
					sender.sendMessage("this automatically charges you and joins you to the game you created");
					return true;
				}

				if (config.isSet("world") && config.isSet("arena-start.x") && config.isSet("arena-start.z") && config.isSet("arena-end.x") && config.isSet("arena-end.z") && config.isSet("altitude"))
				{
					//player/console/other sender wants to create a game.
					if (game != null) 
					{
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}

					double price;


					//testing if the price can parse properly, and if not, using -1.0 as a tag that something went wrong.
					try 
					{
						price = Double.parseDouble(args[1]);
					} 
					catch (NullPointerException np) 
					{

						price = -1.0;

					} 
					catch (NumberFormatException nf) 
					{

						price = -1.0;
					}


					//alerts the sender that the game is invalidated, and does not create a game.
					if (price < 0) 
					{
						sender.sendMessage(ChatColor.DARK_RED + "Invalid price!");
						sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
						return true;
					}

					if(price<config.getInt("min-price"))
					{
						sender.sendMessage(ChatColor.DARK_RED + "Price was less than minimum price!");
						sender.sendMessage(ChatColor.DARK_RED + "No game was created.");
						return true;
					}


					game = new SpleefGame(this, sender, price);
					Bukkit.broadcastMessage(ChatColor.GOLD + sender.getName() + " has created a spleef game! type /spleef join to join for " + ChatColor.DARK_RED + ChatColor.MAGIC + "A" + ChatColor.DARK_RED + ChatColor.UNDERLINE + "$" + price + ChatColor.MAGIC + "A");

					if(sender instanceof Player)
					{
						this.onCommand(sender,command,label,new String[]{"Join"});
					}
					return true;

				}
				else
				{
					sender.sendMessage("Arena area has not been defined yet, ask an admin to set it up.");
					return true;
				}
			}

			//player wants to join the game.
			if (args[0].equalsIgnoreCase("Join"))
			{
				if (game == null) {
					sender.sendMessage(ChatColor.DARK_RED + "There is currently no game, start one with \"/spleef create <Price>\".");
					return true;
				}
				if (game.isInProgress) {
					sender.sendMessage(ChatColor.DARK_RED + "Game is currently in progress, please wait until after it is complete.");
					return true;
				}
				if (sender instanceof Player) {
					Player p = (Player) sender;

					if (game.joinedList.contains(p)) {
						p.sendMessage(ChatColor.DARK_RED + "You are already in the game.");
						return true;
					} else {
						if (economy.has(p, game.price)) {
							economy.withdrawPlayer(p, game.price);
							Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " has joined the spleef game! There are " + ChatColor.DARK_GREEN + (game.joinedList.size() + 1)  + ChatColor.GOLD + " players.");
							return game.addPlayer(p);
						}
						else
						{
							sender.sendMessage(ChatColor.DARK_RED + "You don't have enough money to join the game!");
							return true;
						}

					}
				}
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player, ya dingus! You can't join a game!");
				return true;
			}

			//player wants to leave the queue for the game
			if (args[0].equalsIgnoreCase("Leave")) {
				if (game == null) {
					sender.sendMessage(ChatColor.DARK_RED + "There is currently no game.");
					return true;
				}
				if (game.isInProgress) {
					sender.sendMessage(ChatColor.DARK_RED + "Game is currently in progress, please wait until after it is complete.");
					return true;
				}
				if (sender instanceof Player) {

					Player p = (Player) sender;
					if (game.joinedList.contains(p)) {
						economy.depositPlayer(p,game.price);
						game.pot -= game.price;
						p.sendMessage(ChatColor.DARK_GREEN + "You have left the game and been refunded.");
						return game.removePlayer(p);
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "You are not currently in the game!");
						return true;
					}
				}
				return false;
			}

			//player/console/other sender is attempting to setup an area to be the spleef arena.
			if (args[0].equalsIgnoreCase("SetArena")) {

				if (sender.hasPermission("spleef.admin")) {

					if (args.length == 1) {
						sender.sendMessage("Usage: /spleef setarena <world> <start x> <start z> <end x> <end z> <altitude>");
						return true;
					}

					if (game != null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}

					if (args.length == 7) {
						int[] coords = new int[5];
						for (int i = 2; i < 7; i++) {
							try {
								coords[i - 2] = Integer.parseInt(args[i]);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
								return onCommand(sender, command, label, new String[]{"setarena"});

							}

						}
						if (Bukkit.getServer().getWorld(args[1]) == null) {
							sender.sendMessage(ChatColor.DARK_RED + "That world does not exist!");
							if(sender instanceof Player){
								Player p = (Player) sender;
								sender.sendMessage( "You are currently in \"" + p.getWorld().getName() + "\".");
							}
							return true;
						}
						config.set("world", args[1]);
						config.set("arena-start.x", coords[0]);
						config.set("arena-start.z", coords[1]);
						config.set("arena-end.x", coords[2]);
						config.set("arena-end.z", coords[3]);
						config.set("altitude", coords[4]);
						this.saveConfig();
						sender.sendMessage(ChatColor.GREEN + Integer.toString(Math.abs(coords[0] - coords[2])) + "*" + Integer.toString(Math.abs(coords[1] - coords[3])) + " Arena defined.");

						return true;

					}
					sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
					return onCommand(sender, command, label, new String[]{"setarena"});
				}


			}

			//player/console/other sender is attempting to set the minimum price for joining a game.
			if (args[0].equalsIgnoreCase("SetMinPrice")) {
				if (sender.hasPermission("spleef.admin")) {

					if (args.length == 1) {
						sender.sendMessage("Usage: /spleef setminprice <minprice>");
						return true;
					}

					if (game != null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}

					if (args.length >= 2) {
						try {
							Integer.parseInt(args[1]);
							config.set("min-price", Integer.parseInt(args[1]));
							this.saveConfig();
							sender.sendMessage(ChatColor.GREEN + "Minimum price set to " + config.getInt("min-price"));
							return true;
						} catch (Exception e) {
							sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
							return true;
						}
					}
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
					return true;
				}
			}

			//player/console/other sender is attempting to set the time it takes for the game to start after it is created.
			if (args[0].equalsIgnoreCase("SetStartTime")) {
				if (sender.hasPermission("spleef.admin")) {

					if (args.length == 1) {
						sender.sendMessage("Usage: /spleef setstarttime <starttime>");
						return true;
					}

					if (game != null) {
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}
					if (args.length > 1) {
						try {
							Integer.parseInt(args[1]);
							config.set("start-time", Integer.parseInt(args[1]));
							this.saveConfig();
							sender.sendMessage(ChatColor.GREEN + "Starting time set to " + config.getInt("start-time") + " seconds");
							return true;
						} catch (Exception e) {
							sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
							return true;
						}
					}
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
					return true;
				}
			}

			//player/console/other sender is attempting to set the time it takes for the snow blocks to be able to be broken after players get teleported.
			if (args[0].equalsIgnoreCase("SetCountdownTime"))
			{
				if (sender.hasPermission("spleef.admin"))
				{

					if (args.length == 1) {
						sender.sendMessage("Usage: /spleef countdowntime <countdowntime>");
						return true;
					}

					if (game != null)
					{
						sender.sendMessage(ChatColor.DARK_RED + "There is already a game in progress/waiting to start!");
						return true;
					}
					if (args.length > 1)
					{
						try
						{
							Integer.parseInt(args[1]);
							config.set("countdown-time", Integer.parseInt(args[1]));
							this.saveConfig();
							sender.sendMessage(ChatColor.GREEN + "Countdown time set to " + config.getInt("countdown-time") + " seconds");
							return true;
						}
						catch (Exception e)
						{
							sender.sendMessage(ChatColor.DARK_RED + "Invalid args.");
							return true;
						}
					}
				}
				else
				{
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
					return true;
				}
			}
		}
		return false;
	}
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
