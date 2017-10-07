package anticheat.commands.implemented;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.utils.Color;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class ToggleCommand extends Command {

	public ToggleCommand() {
		super("Daedalus");
	}

	public void onCommand(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (sender.isOp()) {
			if (args.length > 1) {
				String subCommand = args[0];
				String CheckName = args[1];
				if (subCommand.equalsIgnoreCase("toggle")) {
					Checks check = ChecksManager.getCheckByName(CheckName);
					if (check == null) {
						p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Check ' " + CheckName + " ' not found.");
						p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Available checks : Reach, Speed, Velocity.");
						return;
					}
					check.toggle();
					p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + CheckName.toUpperCase()
							+ " state has been set to " + check.getState());
				}
			} else {
				p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " Invalid usage, use /Daedalus toggle CheckName.");

			}
		} else {
			p.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + " You do not have permissions to use this command.");

		}
	}
}