package anticheat.commands;

import org.bukkit.command.CommandSender;

public class Command {
	private String string;

	public Command(String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	public void onCommand(CommandSender sender, String[] args) {
	}
}
