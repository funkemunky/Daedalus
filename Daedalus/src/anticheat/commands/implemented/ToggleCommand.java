package anticheat.commands.implemented;

import anticheat.commands.Command;
import anticheat.detections.Checks;
import anticheat.detections.ChecksManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    	 p.sendMessage(
                         		"§8[§4UCheat§8]" + ChatColor.RED + " Check ' " + CheckName + " ' not found.");
                    	 p.sendMessage(
                         		"§8[§4UCheat§8]" + ChatColor.RED + " Available checks : Reach, Speed, Velocity.");
                        return;
                    }
                    check.toggle();
                    p.sendMessage("§8[§4UCheat§8] " + ChatColor.RED + CheckName.toUpperCase()
                            + " state has been set to " + check.getState());
                }
            } else {
                p.sendMessage(
                		"§8[§4UCheat§8]" + ChatColor.RED + " Invalid usage, use /Daedalus toggle CheckName.");

            }
        } else {
            p.sendMessage(
            		"§8[§4UCheat§8]" + ChatColor.RED + " You do not have permissions to use this command.");

        }
    }
}