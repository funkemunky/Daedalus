package anticheat.commands.implemented;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.Daedalus;
import anticheat.commands.Command;
import anticheat.user.User;
import anticheat.utils.Color;

public class ToggleAlertCommand extends Command {

	public ToggleAlertCommand() {
		super("Daedalus");
	}

	public void onCommand(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		User user = Daedalus.getUserManager().getUser(p.getUniqueId());
		if (user.isStaff()) {
			if (args.length > 1) {
				String subCommand = args[0];
				String secondArgument = args[1];
				if (subCommand.equalsIgnoreCase("Alerts")) {
					if (secondArgument.equalsIgnoreCase("on")) {
						user.setHasAlerts(true);
						p.sendMessage(
								Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Alerts state set to " + ChatColor.GREEN + "true");
					} else if (secondArgument.equalsIgnoreCase("off")) {
						user.setHasAlerts(false);
						p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Alerts state set to " + ChatColor.DARK_RED
								+ "false");

					}

				}
			} else {

				p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Invalid argument!");
			}

		} else {
			p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " You do not have permissions to execute this command!");
		}
	}
}
