package anticheat.commands;

import anticheat.commands.implemented.ToggleCommand;
import anticheat.utils.Color;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
	private List<Command> commands = new ArrayList<Command>();

	public void init() {
		addCommand(new ToggleCommand());
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
					sender.sendMessage(Color.translate("&8[&cDaedalus&8] ") + Color.Red + "Invalid usage.");
					sender.sendMessage(Color.translate("&8[&cDaedalus&8] ") + Color.Red
							+ "Use /Daedalus toggle <CheckName> to enable/disable checks.");
					return;
				}
				cmd.onCommand(sender, args);
			}
		}
	}
}
