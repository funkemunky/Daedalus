package anticheat;

import anticheat.commands.CommandManager;
import anticheat.data.DataManager;
import anticheat.detections.ChecksManager;
import anticheat.events.*;
import anticheat.user.User;
import anticheat.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Daedalus extends JavaPlugin {

	private static ChecksManager checksmanager;
	private static DataManager data;
	private static Daedalus ucheat;
	private static CommandManager commandManager;
	private static UserManager userManager;

	public static DataManager getData() {
		return data;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public static Daedalus getAC() {
		return ucheat;
	}

	public ChecksManager getChecks() {
		return checksmanager;
	}

	public ChecksManager getchecksmanager() {
		return checksmanager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			User user = getUserManager().getUser(p.getUniqueId());
			user.clearData();
			user = null;
		}
	}

	public void onEnable() {
		this.getServer().getConsoleSender().sendMessage("§d------------------------------------------");
		ucheat = this;
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded Main class!");
		userManager = new UserManager();
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded userManager!");
		for (Player p : Bukkit.getOnlinePlayers()) {
			new User(p);
			userManager.add(new User(p));
			User user = userManager.getUser(p.getUniqueId());
			if (user.isStaff()) {
				if (!user.isHasAlerts()) {
					user.setHasAlerts(true);
				}
			}
		}
		checksmanager = new ChecksManager(this);
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded checks!");
		commandManager = new CommandManager();
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded commands!");
		Daedalus.data = new DataManager();
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded players data's !");
		commandManager.init();
		checksmanager.init();
		registerEvents();
		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Registered events!");

		this.getServer().getConsoleSender().sendMessage("§d Daedalus §f Loaded Daedalus !");
		this.getServer().getConsoleSender().sendMessage("§d------------------------------------------");

	}

	/**
	 * Register events here.
	 */

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventPlayerMove(), this);
		pm.registerEvents(new EventPlayerAttack(), this);
		pm.registerEvents(new EventTick(), this);
		pm.registerEvents(new EventJoinQuit(), this);
		pm.registerEvents(new EventPlayerVelocity(), this);
		pm.registerEvents(new EventPlayerInteractEvent(), this);

		data.loaddata();

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p != null) {
						User user = Daedalus.getUserManager().getUser(p.getUniqueId());
						user.setVL(user.getVL() - (user.getVL() / 4));
					}
				}
			}

		}, 0L, 2000L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent());
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p != null) {
						User user = Daedalus.getUserManager().getUser(p.getUniqueId());
						user.setLeftClicks(0);
						user.setRightClicks(0);
					}
				}

			}

		}, 0L, 20L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.getCommandManager().CmdHandler(sender, label, args);
		return true;
	}

}
