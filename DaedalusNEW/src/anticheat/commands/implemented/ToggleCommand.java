package anticheat.commands.implemented;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import anticheat.utils.Color;

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
                    	 p.sendMessage(
                         		Color.translate("&8[&cDaedalus&8]") + Color.Red + " Check ' " + CheckName + " ' not found.");
                    	 p.sendMessage(
                         		Color.translate("&8[&cDaedalus&8]") + Color.Red + " Available checks : ...");
                        return;
                    }
                    check.toggle();
                    p.sendMessage(Color.translate("&8[&cDaedalus&8]") + Color.Red + CheckName.toUpperCase()
                            + " state has been set to " + check.getState());
                }
            } else {
                p.sendMessage(
                		Color.translate("&8[&cDaedalus&8]") + Color.Red + " Invalid usage, use /Daedalus toggle CheckName.");

            }
        } else {
            p.sendMessage(
            		Color.translate("&8[&cDaedalus&8]") + Color.Red + " You do not have permissions to use this command.");

        }
    }
}