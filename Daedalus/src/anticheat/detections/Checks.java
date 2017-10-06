package anticheat.detections;

import anticheat.Daedalus;
import anticheat.user.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Checks {

	public static Daedalus ac;
	public ChecksType type;
	private String name;
	private boolean state;
	public static ArrayList<String> playersToBan = new ArrayList<>();
	private long delay = -1;
	private long interval = 1000;

	public Checks(String name, ChecksType type, Daedalus ac, boolean state) {
		this.name = name;
		Checks.ac = ac;
		this.type = type;
		this.state = state;
		ac.getChecks();
		ChecksManager.getDetections().add(this);
	}

	public boolean getState() {
		return this.state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void toggle() {
		this.setState(!this.state);
	}

	public String getName() {
		return name;
	}

	protected void onEvent(Event event) {
	}

	public void Alert(Player p, String value) {
		long l = System.currentTimeMillis() - this.delay;
		if (l > this.interval) {

			Player[] players = ac.getServer().getOnlinePlayers();
			for (Player player : players) {
				User user = Daedalus.getUserManager().getUser(player.getUniqueId());
				if (user.isStaff()) {
					if (user.isHasAlerts()) {
						TextComponent tp = new TextComponent("§8[§4Daedalus§8] §5" + p.getDisplayName() + " §7is using §a"
								+ getName().toUpperCase() + " §8(§b" + value + "§8)");
						tp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
								"/tp " + p.getName().toString()));
						tp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT,
								new ComponentBuilder("Would you like to teleport to §c" + p.getName()).create()));
						player.spigot().sendMessage(tp);
					}
				}
			}
			this.delay = System.currentTimeMillis();
		}

	}

	public void kick(Player p) {
		Daedalus.getData().addDetecton(p, this);
		if (Daedalus.getUserManager().getUser(p.getUniqueId()).needBan() && !p.isOp()) {
			Daedalus.getAC().getServer().dispatchCommand(Daedalus.getAC().getServer().getConsoleSender(),
					"ipban " + p.getName() + " -s " + "§4Unfair advantage");
			Bukkit.broadcastMessage("§8[§4" + "Daedalus" + "§8]" + " §7Detected §0" + p.getDisplayName()
					+ " §7Cheating and removed them from the network!");
			Daedalus.getUserManager().getUser(p.getUniqueId()).setVL(0);

		}
	}
}