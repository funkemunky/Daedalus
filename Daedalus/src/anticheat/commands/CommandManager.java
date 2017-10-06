package anticheat.commands;

import anticheat.commands.implemented.ToggleAlertCommand;
import anticheat.commands.implemented.ToggleCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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
					sender.sendMessage("§8[§4Daedalust§8] " + ChatColor.RED + "Invalid usage.");
					sender.sendMessage("§8[§4Daedalus§8] " + ChatColor.RED
							+ "Use /Daedalus toggle <CheckName> to enable/disable checks.");
					sender.sendMessage("§8[§4Daedalus§8] " + ChatColor.RED
							+ "Use /Daedalus Alerts on/off to enable/disable alerts.");
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
