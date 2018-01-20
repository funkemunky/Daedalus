package funkemunky.Daedalus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.utils.C;

public class AlertsCommand implements CommandExecutor {
	private Daedalus Daedalus;

	public AlertsCommand(Daedalus Daedalus) {
		this.Daedalus = Daedalus;
	}

	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You have to be a player to run this command!");
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("daedalus.staff")) {
			sender.sendMessage(C.Red + "No permission.");
			return true;
		}
		if (this.Daedalus.hasAlertsOn(player)) {
			this.Daedalus.toggleAlerts(player);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',
					Daedalus.PREFIX + Daedalus.getConfig().getString("alerts.primary") + "Alerts toggled " + C.Red
							+ "off" + Daedalus.getConfig().getString("alerts.primary") + "!"));
		} else {
			this.Daedalus.toggleAlerts(player);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',
					Daedalus.PREFIX + Daedalus.getConfig().getString("alerts.primary") + "Alerts toggled " + C.Green
							+ "on" + Daedalus.getConfig().getString("alerts.primary") + "!"));
		}
		return true;
	}
}
