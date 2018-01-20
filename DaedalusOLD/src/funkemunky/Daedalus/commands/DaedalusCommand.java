package funkemunky.Daedalus.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.DaedalusAPI;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.gui.ChecksGUI;
import funkemunky.Daedalus.utils.C;
import funkemunky.Daedalus.utils.UtilMath;

public class DaedalusCommand implements CommandExecutor {
	private Daedalus Daedalus;

	public DaedalusCommand(Daedalus Daedalus) {
		this.Daedalus = Daedalus;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("daedalus.admin")) {
			sender.sendMessage(C.Red + "This server is using Daedalus b" + Daedalus.getVersion() + " by funkemunky.");
			return true;
		}
		if (args.length == 0) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				ChecksGUI.openDaedalusMain(p);
			} else {
				sender.sendMessage(
						C.Red + "This is for players only! Do /daedalus help to find a command you can do here.");
			}
			return true;
		} else {
			if (args[0].equalsIgnoreCase("violations")) {
				if (sender instanceof Player) {
					String playerName2 = args[1];
					Player player = this.Daedalus.getServer().getPlayer(playerName2);
					Player p = (Player) sender;
					if (player == null || !player.isOnline()) {
						sender.sendMessage(C.Red + "This player is not online!");
						return true;
					}
					ChecksGUI.openStatus(p, player);
				} else {
					sender.sendMessage(C.Red + "This is for players only!");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("dump")) {
				String playerName = args[1];
				String checkName = args[2];
				Check check = null;
				for (Check checkcheck : this.Daedalus.getChecks()) {
					if (checkcheck.getIdentifier().equalsIgnoreCase(checkName)) {
						check = checkcheck;
					}
				}
				if (check == null) {
					sender.sendMessage(C.Red + "This check does not exist!");
					return true;
				}
				String result = check.dump(playerName);
				if (result == null) {
					sender.sendMessage(C.Red + "Error creating dump file for player " + playerName + ".");
				}
				sender.sendMessage(
						Daedalus.PREFIX + C.Gray + "Dropped dump thread at " + C.Yellow + "/dumps/" + result + ".txt");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage(Daedalus.PREFIX + C.Gray + "Reloading Daedalus...");
				Daedalus.reloadConfig();
				sender.sendMessage(Daedalus.PREFIX + C.Green + "Successfully reloaded Daedalus!");
				return true;
			}
			if (args[0].equalsIgnoreCase("clean") || args[0].equalsIgnoreCase("gc")) {
				sender.sendMessage(Daedalus.PREFIX + C.Gray + "Forcing garbage collector..." + C.Gray + "[" + C.Aqua
						+ Daedalus.getLag().getFreeRam() + C.Gray + "/" + C.Red + Daedalus.getLag().getMaxRam() + C.Gray
						+ "]");
				System.gc();
				sender.sendMessage(Daedalus.PREFIX + C.Green + "Completed garbage collector! " + C.Gray + "[" + C.Aqua
						+ UtilMath.trim(3, Daedalus.getLag().getFreeRam()) + C.Gray + "/" + C.Red
						+ UtilMath.trim(3, Daedalus.getLag().getMaxRam()) + C.Gray + "]");
				return true;
			}
			if (args[0].equalsIgnoreCase("lag") || args[0].equalsIgnoreCase("performance")) {
				sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
				sender.sendMessage(C.Red + C.Bold + "Performance Usage:");
				sender.sendMessage("");
				sender.sendMessage(C.Gray + "TPS: " + C.White + UtilMath.trim(2, Daedalus.getLag().getTPS()));
				sender.sendMessage(C.Gray + "Free Ram: " + C.White + Daedalus.getLag().getFreeRam() + "MB");
				sender.sendMessage(C.Gray + "Max Ram: " + C.White + Daedalus.getLag().getMaxRam() + "MB");
				sender.sendMessage(C.Gray + "Used Ram: " + C.White
						+ Math.abs(Daedalus.getLag().getMaxRam() - Daedalus.getLag().getFreeRam()) + "MB");
				if (Math.abs(
						Daedalus.getLag().getMaxRam() - Daedalus.getLag().getFreeRam()) > Daedalus.getLag().getMaxRam()
								/ 2.1) {
					sender.sendMessage(
							C.Aqua + C.Italics + "It is recommended you do /daedalus clean to clear up some RAM.");
				}
				sender.sendMessage(
						Daedalus.getLag().getLag() > 20 ? C.Red + "Server Usage: " + Daedalus.getLag().getLag() + "%"
								: C.Green + "Server Usage: " + Daedalus.getLag().getLag() + "%");

				sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
				return true;
			}
			if (args[0].equalsIgnoreCase("test")) {
				sender.sendMessage(String.valueOf(10 % 7));
				return true;
			}
			if (args[0].equalsIgnoreCase("bans")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					ChecksGUI.openBans(p);
				} else {
					sender.sendMessage(C.Red + "This is for players only!");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
				sender.sendMessage(C.Red + C.Bold + "Daedalus Help:");
				sender.sendMessage(" ");
				sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " help" + C.Gray + "  - View the help page.");
				sender.sendMessage(
						C.Gray + "/daedalus" + C.Reset + " dump" + C.Gray + " - Dump a check log of a player.");
				sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " ping" + C.Gray + "  - Get your ping.");
				sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " bans" + C.Gray + " - Lists bans this restart.");
				sender.sendMessage(
						C.Gray + "/daedalus" + C.Reset + " clean" + C.Gray + " - Run Java Garbage-Collector.");
				sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " reload" + C.Gray + "   - Reload Daedalus.");
				sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " violations <player>" + C.Gray
						+ " - Gets the violations of a player.");
				sender.sendMessage(
						C.Gray + "/daedalus" + C.Reset + " lag" + C.Gray + "  - Get server performance info.");
				sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
				return true;
			}
			if (args[0].equalsIgnoreCase("ping")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 1) {
						sender.sendMessage(Daedalus.PREFIX + C.DGray + "[Vanilla] " + C.Gray + "Your ping: " + C.Red
								+ Daedalus.getLag().getPing(p));
						sender.sendMessage(Daedalus.PREFIX + C.DGray + "[Daedalus] " + C.Gray + "Your ping: " + C.Red
								+ DaedalusAPI.getPing(p));
						return true;
					}
					if (args.length == 2) {
						Player target = Bukkit.getPlayer(args[1]);
						if (target == null) {
							sender.sendMessage(Daedalus.PREFIX + C.Red + "That player is not online!");
							return true;
						}
						sender.sendMessage(Daedalus.PREFIX + C.White + target.getName() + "'s " + C.Gray + " ping: "
								+ C.Red + Daedalus.getLag().getPing(target));
						sender.sendMessage(Daedalus.PREFIX + C.White + target.getName() + "'s " + C.Gray + " ping: "
								+ C.Red + DaedalusAPI.getPing(target));
						return true;
					}
					sender.sendMessage(Daedalus.PREFIX + C.Red + "Incorrect arguments. Usage: /daedalus ping [player]");
				} else {
					sender.sendMessage(C.Red + "This is for players only!");
				}
				return true;
			}
			sender.sendMessage(C.Red + "Unknown argument '/" + alias + " " + args[0] + "'! Do " + C.Italics
					+ "/daedalus help " + C.Red + "for more info!");
		}
		return true;
	}
}