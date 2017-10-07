package anticheat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import anticheat.commands.implemented.ToggleAlertCommand;
import anticheat.commands.implemented.ToggleCommand;
import anticheat.utils.Color;

public class CommandManager {
	private List<Command> commands = new ArrayList<Command>();

	public void init() {
		addCommand(new ToggleCommand());
		addCommand(new ToggleAlertCommand());
	}

	private List<Command> getCommands() {
		return this.commands;
	}

	private void addCommand(Command command) {
		this.getCommands().add(command);
	}

	public void CmdHandler(CommandSender sender, String label, String[] args) {
		for (Command cmd : getCommands()) {
			if (cmd.getString().equalsIgnoreCase(label)) {
				if (args.length < 1) {
					sender.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED + "Invalid usage.");
					sender.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED
							+ "Use /Daedalus toggle <CheckName> to enable/disable checks.");
					sender.sendMessage(Color.Gray + "[" + Color.Red + Color.Bold + "Daedalus" + Color.Gray + "] " + ChatColor.RED
							+ "Use /Daedalus Alerts on/off to enable/disable alerts.");
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
