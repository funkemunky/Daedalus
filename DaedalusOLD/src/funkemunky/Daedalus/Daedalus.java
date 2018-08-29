package funkemunky.Daedalus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.combat.AutoclickerA;
import funkemunky.Daedalus.check.combat.AutoclickerB;
import funkemunky.Daedalus.check.combat.Crits;
import funkemunky.Daedalus.check.combat.FastBow;
import funkemunky.Daedalus.check.combat.HitBoxes;
import funkemunky.Daedalus.check.combat.KillAuraA;
import funkemunky.Daedalus.check.combat.KillAuraB;
import funkemunky.Daedalus.check.combat.KillAuraC;
import funkemunky.Daedalus.check.combat.KillAuraD;
import funkemunky.Daedalus.check.combat.KillAuraE;
import funkemunky.Daedalus.check.combat.KillauraF;
import funkemunky.Daedalus.check.combat.ReachA;
import funkemunky.Daedalus.check.combat.ReachB;
import funkemunky.Daedalus.check.combat.ReachC;
import funkemunky.Daedalus.check.combat.Regen;
import funkemunky.Daedalus.check.combat.Twitch;
import funkemunky.Daedalus.check.movement.AscensionA;
import funkemunky.Daedalus.check.movement.AscensionB;
import funkemunky.Daedalus.check.movement.FastLadder;
import funkemunky.Daedalus.check.movement.Fly;
import funkemunky.Daedalus.check.movement.Glide;
import funkemunky.Daedalus.check.movement.Jesus;
import funkemunky.Daedalus.check.movement.NoFall;
import funkemunky.Daedalus.check.movement.NoSlowdown;
import funkemunky.Daedalus.check.movement.PhaseA;
import funkemunky.Daedalus.check.movement.SpeedA;
import funkemunky.Daedalus.check.movement.SpeedB;
import funkemunky.Daedalus.check.movement.Spider;
import funkemunky.Daedalus.check.movement.Step;
import funkemunky.Daedalus.check.movement.VClip;
import funkemunky.Daedalus.check.other.Crash;
import funkemunky.Daedalus.check.other.Exploit;
import funkemunky.Daedalus.check.other.FreeCam;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.check.other.MorePackets;
import funkemunky.Daedalus.check.other.Sneak;
import funkemunky.Daedalus.check.other.TimerB;
import funkemunky.Daedalus.check.other.Vape;
import funkemunky.Daedalus.commands.AlertsCommand;
import funkemunky.Daedalus.commands.AutobanCommand;
import funkemunky.Daedalus.commands.DaedalusCommand;
import funkemunky.Daedalus.commands.GetLogCommand;
import funkemunky.Daedalus.gui.ChecksGUI;
import funkemunky.Daedalus.lag.LagCore;
import funkemunky.Daedalus.packets.PacketCore;
import funkemunky.Daedalus.update.UpdateEvent;
import funkemunky.Daedalus.update.UpdateType;
import funkemunky.Daedalus.update.Updater;
import funkemunky.Daedalus.utils.C;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.ConfigFile;
import funkemunky.Daedalus.utils.TxtFile;
import funkemunky.Daedalus.utils.UtilActionMessage;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilServer;

public class Daedalus extends JavaPlugin implements Listener {
	public static Daedalus Instance;
	public String PREFIX;
	public Updater updater;
	public PacketCore packet;
	public LagCore lag;
	public List<Check> Checks;
	private static ConfigFile file;
	public Map<UUID, Map<Check, Integer>> Violations;
	public Map<UUID, Map<Check, Long>> ViolationReset;
	public List<Player> AlertsOn;
	public Map<Player, Map.Entry<Check, Long>> AutoBan;
	public Map<String, Check> NamesBanned;
	Random rand;
	private Check check;
	public TxtFile autobanMessages;
	public Map<UUID, Long> LastVelocity;
	public ArrayList<UUID> hasInvOpen = new ArrayList<UUID>();
	public Integer pingToCancel = getConfig().getInt("settings.latency.ping");
	public Integer tpsToCancel = getConfig().getInt("settings.latency.tps");

	public Daedalus() {
		super();
		this.Checks = new ArrayList<Check>();
		this.Violations = new HashMap<UUID, Map<Check, Integer>>();
		this.ViolationReset = new HashMap<UUID, Map<Check, Long>>();
		this.AlertsOn = new ArrayList<Player>();
		this.AutoBan = new HashMap<Player, Map.Entry<Check, Long>>();
		this.NamesBanned = new HashMap<String, Check>();
		this.rand = new Random();
		this.LastVelocity = new HashMap<UUID, Long>();
	}

	public void addChecks() {
		this.Checks.add(new AscensionA(this));
		this.Checks.add(new AscensionB(this));
		this.Checks.add(new SpeedA(this));
		this.Checks.add(new SpeedB(this));
		this.Checks.add(new Fly(this));
		this.Checks.add(new Step(this));
		this.Checks.add(new Regen(this));
		this.Checks.add(new NoFall(this));
		this.Checks.add(new PhaseA(this));
		this.Checks.add(new VClip(this));
		this.Checks.add(new KillAuraA(this));
		this.Checks.add(new KillAuraB(this));
		this.Checks.add(new KillAuraC(this));
		this.Checks.add(new KillAuraD(this));
		this.Checks.add(new KillAuraE(this));
		this.Checks.add(new KillauraF(this));
		this.Checks.add(new HitBoxes(this));
		this.Checks.add(new AutoclickerA(this));
		this.Checks.add(new AutoclickerB(this));
		this.Checks.add(new FastBow(this));
		this.Checks.add(new Twitch(this));
		this.Checks.add(new NoSlowdown(this));
		this.Checks.add(new Crits(this));
		this.Checks.add(new ReachA(this));
		this.Checks.add(new ReachB(this));
		this.Checks.add(new ReachC(this));
		this.Checks.add(new MorePackets(this));
		this.Checks.add(new funkemunky.Daedalus.check.other.Timer(this));
		this.Checks.add(new TimerB(this));
		this.Checks.add(new Sneak(this));
		this.Checks.add(new Crash(this));
		this.Checks.add(new FastLadder(this));
		this.Checks.add(new Jesus(this));
		this.Checks.add(new Exploit(this));
		this.Checks.add(new Spider(this));
		this.Checks.add(new Vape(this));
		this.Checks.add(new FreeCam(this));
	}

	public void onEnable() {
		Daedalus.Instance = this;
		this.addChecks();
		this.packet = new PacketCore(this);
		this.lag = new LagCore(this);
		this.updater = new Updater(this);
		Vape vapers = new Vape(this);
		new DaedalusAPI(this);
		this.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "LOLIMAHCKER",
				(PluginMessageListener) vapers);
		for (final Check check : this.Checks) {
			if (check.isEnabled()) {
				this.RegisterListener((Listener) check);
			}
		}
		File file = new File(getDataFolder(), "config.yml");
		this.getCommand("alerts").setExecutor(new AlertsCommand(this));
		this.getCommand("autoban").setExecutor(new AutobanCommand(this));
		this.getCommand("daedalus").setExecutor(new DaedalusCommand(this));
		this.getCommand("getLog").setExecutor(new GetLogCommand(this));
		Bukkit.getServer().getPluginManager().registerEvents(new ChecksGUI(this), this);
		this.RegisterListener(this);
		Bukkit.getServer().getPluginManager().registerEvents(new Latency(this), this);
		if (!file.exists()) {
			this.getConfig().addDefault("bans", 0);
			this.getConfig().addDefault("testmode", false);
			this.getConfig().addDefault("prefix", "&8[&c&lDaedalus&8] ");
			this.getConfig().addDefault("alerts.primary", "&7");
			this.getConfig().addDefault("alerts.secondary", "&c");
			this.getConfig().addDefault("alerts.checkColor", "&b");
			this.getConfig().addDefault("bancmd", "ban %player% [Daedalus] Unfair Advantage: %check%");
			this.getConfig().addDefault("broadcastmsg",
					"&c&lDaedalus &7has detected &c%player% &7to be cheating and has been removed from the network.");
			this.getConfig().addDefault("settings.broadcastResetViolationsMsg", true);
			this.getConfig().addDefault("settings.violationResetTime", 60);
			this.getConfig().addDefault("settings.resetViolationsAutomatically", true);
			this.getConfig().addDefault("settings.gui.checkered", true);
			this.getConfig().addDefault("settings.latency.ping", 300);
			this.getConfig().addDefault("settings.latency.tps", 17);
			this.getConfig().addDefault("settings.sotwMode", false);
			this.getConfig().addDefault("hwid", "");
			for (Check check : Checks) {
				this.getConfig().addDefault("checks." + check.getIdentifier() + ".enabled", check.isEnabled());
				this.getConfig().addDefault("checks." + check.getIdentifier() + ".bannable", check.isBannable());
				this.getConfig().addDefault("checks." + check.getIdentifier() + ".banTimer", check.hasBanTimer());
				this.getConfig().addDefault("checks." + check.getIdentifier() + ".maxViolations",
						check.getMaxViolations());
			}
			this.getConfig().addDefault("checks.Phase.pearlFix", true);
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
		for (Check check : Checks) {
			if (!getConfig().isConfigurationSection("checks." + check.getIdentifier())) {
				this.getConfig().set("checks." + check.getIdentifier() + ".enabled", check.isEnabled());
				this.getConfig().set("checks." + check.getIdentifier() + ".bannable", check.isBannable());
				this.getConfig().set("checks." + check.getIdentifier() + ".banTimer", check.hasBanTimer());
				this.getConfig().set("checks." + check.getIdentifier() + ".maxViolations", check.getMaxViolations());
				this.saveConfig();
			}
		}
		this.PREFIX = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
		// Reset Violations Counter
		new BukkitRunnable() {
			public void run() {
				getLogger().log(Level.INFO, "Reset Violations!");
				if (getConfig().getBoolean("resetViolationsAutomatically")) {
					if (getConfig().getBoolean("settings.broadcastResetViolationsMsg")) {
						for (Player online : Bukkit.getServer().getOnlinePlayers()) {
							if (online.hasPermission("daedalus.admin") && hasAlertsOn(online)) {
								online.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&',
										"&7Reset violations for all players!"));
							}
						}
					}
					resetAllViolations();
				}
			}
		}.runTaskTimerAsynchronously(this, 0L,
				TimeUnit.SECONDS.toMillis(getConfig().getLong("settings.violationResetTime")));

	}

	public void resetDumps(Player player) {
		for (Check check : Checks) {
			if (check.hasDump(player)) {
				check.clearDump(player);
			}
		}
	}

	public void resetAllViolations() {
		this.Violations.clear();
		this.ViolationReset.clear();
	}

	public String resetData() {
		try {
			resetAllViolations();
			if (!AutoclickerB.Clicks.isEmpty())
				AutoclickerB.Clicks.clear();
			if (!AutoclickerB.LastMS.isEmpty())
				AutoclickerB.LastMS.clear();
			if (!AutoclickerB.ClickTicks.isEmpty())
				AutoclickerB.ClickTicks.clear();
			if (!Crits.CritTicks.isEmpty())
				Crits.CritTicks.clear();
			if (!KillAuraA.ClickTicks.isEmpty())
				KillAuraA.ClickTicks.clear();
			if (!KillAuraA.Clicks.isEmpty())
				KillAuraA.Clicks.clear();
			if (!KillAuraA.LastMS.isEmpty())
				KillAuraA.LastMS.clear();
			if (!KillAuraB.AuraTicks.isEmpty())
				KillAuraB.AuraTicks.clear();
			if (!KillAuraC.Differences.isEmpty())
				KillAuraC.Differences.clear();
			if (!KillAuraC.LastLocation.isEmpty())
				KillAuraC.LastLocation.clear();
			if (!KillAuraC.AimbotTicks.isEmpty())
				KillAuraC.AimbotTicks.clear();
			if (!KillAuraE.lastAttack.isEmpty())
				KillAuraE.lastAttack.clear();
			if (!KillauraF.counts.isEmpty())
				KillauraF.counts.clear();
			if (!Regen.FastHealTicks.isEmpty())
				Regen.FastHealTicks.clear();
			if (!Regen.LastHeal.isEmpty())
				Regen.LastHeal.clear();
			if (!AscensionA.AscensionTicks.isEmpty())
				AscensionA.AscensionTicks.clear();
			if (!Fly.flyTicksA.isEmpty())
				Fly.flyTicksA.clear();
			if (!Glide.flyTicks.isEmpty())
				Glide.flyTicks.clear();
			if (!NoFall.FallDistance.isEmpty())
				NoFall.FallDistance.clear();
			if (!NoFall.NoFallTicks.isEmpty())
				NoFall.NoFallTicks.clear();
			if (!NoSlowdown.speedTicks.isEmpty())
				NoSlowdown.speedTicks.clear();
			if (!SpeedA.speedTicks.isEmpty())
				SpeedA.speedTicks.clear();
			if (!SpeedA.tooFastTicks.isEmpty())
				SpeedA.tooFastTicks.clear();
			if (!SpeedA.lastHit.isEmpty())
				SpeedA.lastHit.isEmpty();
			if (!MorePackets.lastPacket.isEmpty())
				MorePackets.lastPacket.clear();
			if (!MorePackets.packetTicks.isEmpty())
				MorePackets.packetTicks.clear();
			if (!Sneak.sneakTicks.isEmpty())
				Sneak.sneakTicks.clear();
			if (!HitBoxes.count.isEmpty())
				HitBoxes.count.clear();
			if (!HitBoxes.lastHit.isEmpty())
				HitBoxes.lastHit.clear();
			if (!HitBoxes.yawDif.isEmpty())
				HitBoxes.yawDif.clear();
			if (!FastBow.count.isEmpty())
				FastBow.count.clear();
		} catch (Exception e) {
			return ChatColor.translateAlternateColorCodes('&', PREFIX + C.Red + "Unknown error occured!");
		}
		return ChatColor.translateAlternateColorCodes('&', PREFIX + C.Green + "Successfully reset data!");
	}

	public Integer getPingCancel() {
		return pingToCancel;
	}

	public Integer getTPSCancel() {
		return tpsToCancel;
	}

	public List<Check> getChecks() {
		return new ArrayList<Check>(this.Checks);
	}

	public boolean isCheckingUpdates() {
		return this.getConfig().getBoolean("settings.checkUpdates");
	}

	public String getVersion() {
		return this.getDescription().getVersion();
	}

	public boolean isSotwMode() {
		return getConfig().getBoolean("settings.sotwMode");
	}

	public boolean hasNewVersion() {
		if (!this.getVersion().equalsIgnoreCase(getPasteVersion())) {
			return true;
		}
		return false;
	}

	public String getPasteVersion() {
		try {
			URL url = new URL(UtilMath.decrypt("aHR0cDovL3Bhc3RlYmluLmNvbS9yYXcvQU4yWEtqTlM="));
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			line = in.readLine();
			if (line != null) {
				return line;
			}
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, UtilMath.decrypt("RXJyb3IhIENvdWxkIG5vdCBjaGVjayBmb3IgYSBuZXcgdmVyc2lvbiE="));
		}
		return "Error";
	}

	public Map<String, Check> getNamesBanned() {
		return new HashMap<String, Check>(this.NamesBanned);
	}

	public String getCraftBukkitVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	public List<Player> getAutobanQueue() {
		return new ArrayList<Player>(this.AutoBan.keySet());
	}

	public void createLog(Player player, Check checkBanned) {
		TxtFile logFile = new TxtFile(this, File.separator + "logs", player.getName());
		Map<Check, Integer> Checks = getViolations(player);
		logFile.addLine("------------------- Player was banned for: " + checkBanned.getName() + " -------------------");
		logFile.addLine("Set off checks:");
		for (Check check : Checks.keySet()) {
			Integer Violations = Checks.get(check);
			logFile.addLine("- " + check.getName() + " (" + Violations + " VL)");
		}
		logFile.addLine(" ");
		logFile.addLine("Dump-Log for all checks set off:");
		for (Check check : Checks.keySet()) {
			logFile.addLine(" ");
			logFile.addLine(check.getName() + ":");
			if (check.getDump(player) != null) {
				for (String line : check.getDump(player)) {
					logFile.addLine(line);
				}
			} else {
				logFile.addLine("Checks had no dump logs.!");
			}
			logFile.addLine(" ");
		}
		logFile.write();
	}

	public void removeFromAutobanQueue(Player player) {
		this.AutoBan.remove(player);
	}

	public void removeViolations(Player player) {
		this.Violations.remove(player.getUniqueId());
	}

	public boolean hasAlertsOn(Player player) {
		return this.AlertsOn.contains(player);
	}

	public void toggleAlerts(Player player) {
		if (this.hasAlertsOn(player)) {
			this.AlertsOn.remove(player);
		} else {
			this.AlertsOn.add(player);
		}
	}

	public LagCore getLag() {
		return this.lag;
	}

	@EventHandler
	public void Join(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPermission("daedalus.staff")) {
			return;
		}
		this.AlertsOn.add(e.getPlayer());
	}

	@EventHandler
	public void autobanupdate(UpdateEvent event) {
		if (!event.getType().equals(UpdateType.SEC)) {
			return;
		}
		Map<Player, Map.Entry<Check, Long>> AutoBan = new HashMap<Player, Map.Entry<Check, Long>>(this.AutoBan);
		for (Player player : AutoBan.keySet()) {
			if (player == null || !player.isOnline()) {
				this.AutoBan.remove(player);
			} else {
				Long time = AutoBan.get(player).getValue();
				if (System.currentTimeMillis() < time) {
					continue;
				}
				this.autobanOver(player);
			}
		}
		final Map<UUID, Map<Check, Long>> ViolationResets = new HashMap<UUID, Map<Check, Long>>(this.ViolationReset);
		for (UUID uid : ViolationResets.keySet()) {
			if (!this.Violations.containsKey(uid)) {
				continue;
			}
			Map<Check, Long> Checks = new HashMap<Check, Long>(ViolationResets.get(uid));
			for (Check check : Checks.keySet()) {
				Long time2 = Checks.get(check);
				if (System.currentTimeMillis() >= time2) {
					this.ViolationReset.get(uid).remove(check);
					this.Violations.get(uid).remove(check);
				}
			}
		}
	}

	public Integer getViolations(Player player, Check check) {
		if (this.Violations.containsKey(player.getUniqueId())) {
			return this.Violations.get(player.getUniqueId()).get(check);
		}
		return 0;
	}

	public Map<Check, Integer> getViolations(Player player) {
		if (this.Violations.containsKey(player.getUniqueId())) {
			return new HashMap<Check, Integer>(this.Violations.get(player.getUniqueId()));
		}
		return null;
	}

	private void wqminoiwn() {
		Bukkit.getPluginManager().disablePlugin(this);
	}

	public void addViolation(Player player, Check check) {
		Map<Check, Integer> map = new HashMap<Check, Integer>();
		if (this.Violations.containsKey(player.getUniqueId())) {
			map = this.Violations.get(player.getUniqueId());
		}
		if (!map.containsKey(check)) {
			map.put(check, 1);
		} else {
			map.put(check, map.get(check) + 1);
		}
		this.Violations.put(player.getUniqueId(), map);
	}

	public void removeViolations(Player player, Check check) {
		if (this.Violations.containsKey(player.getUniqueId())) {
			this.Violations.get(player.getUniqueId()).remove(check);
		}
	}

	public void setViolationResetTime(Player player, Check check, long time) {
		Map<Check, Long> map = new HashMap<Check, Long>();
		if (this.ViolationReset.containsKey(player.getUniqueId())) {
			map = this.ViolationReset.get(player.getUniqueId());
		}
		map.put(check, time);
		this.ViolationReset.put(player.getUniqueId(), map);
	}

	public void getAPI() {
		try {
			URL url = new URL(UtilMath.decrypt("aHR0cHM6Ly9wYXN0ZWJpbi5jb20vcmF3L3pFZ1lLQVBu"));
			ArrayList<Object> lines = new ArrayList<Object>();
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			if (!lines.contains(this.getConfig().getString("hwid")) && this.getConfig().getString("hwid") != null) {

				getLogger().log(Level.SEVERE, wngnq);
				wqminoiwn();
			} else if (getConfig().getString("hwid") == null) {
				getLogger().log(Level.SEVERE, UtilMath.decrypt("QWRkIGFuIEhXSUQgaW4gdGhlIGNvbmZpZyENCg=="));
				wqminoiwn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE, UtilMath.decrypt("RXJyb3IhIERpc2FibGluZyBwbHVnaW4u"));
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	public void autobanOver(Player player) {
		final Map<Player, Map.Entry<Check, Long>> AutoBan = new HashMap<Player, Map.Entry<Check, Long>>(this.AutoBan);
		if (AutoBan.containsKey(player)) {
			this.banPlayer(player, AutoBan.get(player).getKey());
			this.AutoBan.remove(player);
		}
	}

	public void autoban(Check check, Player player) {
		if (this.lag.getTPS() < 17.0) {
			return;
		}
		if (check.hasBanTimer()) {
			if (this.AutoBan.containsKey(player)) {
				return;
			}
			this.AutoBan.put(player,
					new AbstractMap.SimpleEntry<Check, Long>(check, System.currentTimeMillis() + 10000L));
			System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName()
					+ " will be banned in 15s for " + check.getName() + ".");
			final UtilActionMessage msg = new UtilActionMessage();
			msg.addText(PREFIX);
			msg.addText(ChatColor.translateAlternateColorCodes('&',
					getConfig().getString("alerts.secondary") + player.getName()))
					.addHoverText(C.Gray + "(Click to teleport to " + C.Red + player.getName() + C.Gray + ")")
					.setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
			msg.addText(ChatColor.translateAlternateColorCodes('&',
					getConfig().getString("alerts.primary") + " set off " + getConfig().getString("alerts.secondary")
							+ check.getName() + getConfig().getString("alerts.primary") + " and will "
							+ getConfig().getString("alerts.primary") + "be " + getConfig().getString("alerts.primary")
							+ "banned" + getConfig().getString("alerts.primary") + " if you don't take action. "
							+ C.DGray + C.Bold + "["));
			msg.addText(ChatColor.translateAlternateColorCodes('&',
					getConfig().getString("alerts.secondary") + C.Bold + "ban"))
					.addHoverText(C.Gray + "Autoban " + C.Green + player.getName())
					.setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban ban " + player.getName());
			msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + " or ");
			msg.addText(C.Green + C.Bold + "cancel").addHoverText(C.Gray + "Click to Cancel")
					.setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban cancel " + player.getName());
			msg.addText(C.DGray + C.Bold + "]");
			ArrayList<Player> players;
			for (int length = (players = UtilServer.getOnlinePlayers()).size(), i = 0; i < length; ++i) {
				Player playerplayer = players.get(i);
				if (playerplayer.hasPermission("daedalus.staff")) {
					msg.sendToPlayer(playerplayer);
				}
			}
		} else {
			this.banPlayer(player, check);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void Velocity(PlayerVelocityEvent event) {
		this.LastVelocity.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	public void banPlayer(Player player, Check check) {
		if (!getConfig().getBoolean("testmode")) {
			this.createLog(player, check);
		}
		if (NamesBanned.containsKey(player.getName()) && !getConfig().getBoolean("testmode")) {
			return;
		}
		this.NamesBanned.put(player.getName(), check);
		this.removeViolations(player, check);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (NamesBanned.containsKey(player.getName()) && getConfig().getBoolean("testmode")) {
					return;
				}
				if (Latency.getLag(player) < 250) {
					if (getConfig().getBoolean("testmode")) {
						player.sendMessage(
								PREFIX + C.Gray + "You would be banned right now for: " + C.Red + check.getName());
					} else {
						if (!getConfig().getString("broadcastmsg").equalsIgnoreCase("")) {
							Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
									getConfig().getString("broadcastmsg").replaceAll("%player%", player.getName())));
						}
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("bancmd")
								.replaceAll("%player%", player.getName()).replaceAll("%check%", check.getName()));
					}
				}
				NamesBanned.put(player.getName(), check);
			}
		}.runTaskLater(this, 10L);
		if (Violations.containsKey(player))
			this.Violations.remove(player);
		this.getConfig().set("bans", (Object) (this.getConfig().getInt("bans") + 1));
		this.saveConfig();
	}

	public void alert(String message) {
		for (Player playerplayer : this.AlertsOn) {
			playerplayer.sendMessage(String.valueOf(PREFIX) + message);
		}
	}

	public String wngnq = UtilMath.decrypt(
			"SW5jb3JyZWN0IEhXSUQhIERpc2FibGluZyBwbHVnaW4uIElmIHlvdSBuZWVkIGFuIEhXSUQsIGdldCBvbmUgZnJvbSBmdW5rZW11bmt5ISBFLW1haWwgZnVua2VtdW5reWJpekBnbWFpbC5jb20gb3IgbWVzc2FnZSBmdW5rZW11bmt5IG9uIFNwaWdvdE1DIG9yIE1DTWFya2V0Lg==");

	public void logCheat(Check check, Player player, String hoverabletext, Chance chance, String... identifiers) {
		String a = "";
		if (identifiers != null) {
			for (String b : identifiers) {
				a = a + " (" + b + ")";
			}
		}
		this.addViolation(player, check);
		this.setViolationResetTime(player, check, System.currentTimeMillis() + check.getViolationResetTime());
		Integer violations = this.getViolations(player, check);
		System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName() + " failed "
				+ (check.isJudgmentDay() ? "JD check " : "") + check.getName() + a + " [" + violations + " VL]");
		if (violations >= check.getViolationsToNotify()) {
			UtilActionMessage msg = new UtilActionMessage();
			msg.addText(PREFIX);
			msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary"))
					+ player.getName())
					.addHoverText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary"))
							+ "(Click to teleport to "
							+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary"))
							+ player.getName()
							+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary"))
							+ ")")
					.setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
			msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary"))
					+ " failed " + (check.isJudgmentDay() ? "JD check " : ""));
			UtilActionMessage.AMText CheckText = msg
					.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.checkColor"))
							+ check.getName());
			if (hoverabletext != null) {
				CheckText.addHoverText(hoverabletext);
			}
			msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")) + a
					+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + " ");
			msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + "["
					+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary"))
					+ violations + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary"))
					+ " VL]");
			msg.addText(ChatColor.translateAlternateColorCodes('&', " " + getConfig().getString("alerts.primary")) + "("
					+ DaedalusAPI.getChanceString(chance)
					+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + ")");
			if (violations % check.getViolationsToNotify() == 0) {
				if (getConfig().getBoolean("testmode") == true) {
					msg.sendToPlayer(player);
				} else {
					for (Player playerplayer : this.AlertsOn) {
						if (check.isJudgmentDay() && !playerplayer.hasPermission("daedalus.admin")) {
							continue;
						}
						msg.sendToPlayer(playerplayer);
					}
				}
			}
			if (check.isJudgmentDay()) {
				return;
			}
			if (violations > check.getMaxViolations() && check.isBannable()) {
				this.autoban(check, player);
			}
		}
	}

	public void RegisterListener(Listener listener) {
		this.getServer().getPluginManager().registerEvents(listener, (Plugin) this);
	}

	public Map<UUID, Long> getLastVelocity() {
		return this.LastVelocity;
	}

	@EventHandler
	public void Kick(PlayerKickEvent event) {
		if (event.getReason().equals("Flying is not enabled on this server")) {
			this.alert(String.valueOf(C.Gray) + event.getPlayer().getName() + " was kicked for flying");
		}
	}
}