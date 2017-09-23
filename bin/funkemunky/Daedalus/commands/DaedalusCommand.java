package funkemunky.Daedalus.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class DaedalusCommand implements CommandExecutor
{
    private Daedalus Daedalus;

    public DaedalusCommand(Daedalus Daedalus) {
        this.Daedalus = Daedalus;
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String alias,  String[] args) {
        if (!sender.hasPermission("daedalus.admin")) {
            sender.sendMessage(C.Red + "This server is using Daedalus b" + Daedalus.getVersion() + " by funkemunky.");
            return true;
        }
        if (args.length == 0) {
        	if(sender instanceof Player) {
        		Player p = (Player) sender;
            	ChecksGUI.openDaedalusMain(p);
        	} else {
        		sender.sendMessage(C.Red + "This is for players only! Do /daedalus help to find a command you can do here.");
        	}
            return true;
        } else {
        	if(args.length == 1) {
        		if(args[0].equalsIgnoreCase("status")) {
                    sender.sendMessage(C.Gray + C.Strike + "----------------------------------------------------");
                    sender.sendMessage(C.Red + C.Bold + "Daedalus Anticheat " + C.Red + C.Italics + "b" + Daedalus.getDescription().getVersion() + C.DGray + " by funkemunky");
                    sender.sendMessage(" ");
                    sender.sendMessage(C.Gray + "TPS: " + C.Red + UtilMath.trim(1, this.Daedalus.getLag().getTPS()));
                    sender.sendMessage(C.Gray + "Server Load: " + C.Red + this.Daedalus.getLag().getLag() + "%");
                    sender.sendMessage(C.Gray + "Bans Today: " + C.Red + this.Daedalus.getNamesBanned().size());
                    sender.sendMessage(C.Gray + "Total Bans: " + C.Red + this.Daedalus.getConfig().getInt("bans"));
                    sender.sendMessage(" ");
                    List<Check> autobanChecks = new ArrayList<Check>();
                    for (Check check2 : this.Daedalus.getChecks()) {
                        if (check2.isBannable() && !check2.hasBanTimer() && !check2.isJudgmentDay()) {
                            autobanChecks.add(check2);
                        }
                    }
                     List<Check> timerChecks = new ArrayList<Check>();
                    for (Check check3 : this.Daedalus.getChecks()) {
                        if (check3.isBannable() && check3.hasBanTimer() && !check3.isJudgmentDay()) {
                            timerChecks.add(check3);
                        }
                    }
                     List<Check> silentChecks = new ArrayList<Check>();
                    for (Check check4 : this.Daedalus.getChecks()) {
                        if (!check4.isBannable() && !check4.isJudgmentDay()) {
                            silentChecks.add(check4);
                        }
                    }
                     List<Check> JDchecks = new ArrayList<Check>();
                    for (Check check5 : this.Daedalus.getChecks()) {
                        if (check5.isJudgmentDay()) {
                            JDchecks.add(check5);
                        }
                    }
                    String checks = C.Gray + "Autoban: ";
                    for (int j = 0; j < autobanChecks.size(); ++j) {
                        Check check6 = autobanChecks.get(j);
                        checks = checks + (check6.isEnabled() ? C.Green : C.Red) + check6.getName() + ((autobanChecks.size() - 1 == j) ? "" : (C.Gray + ", "));
                    }
                    String checks2 = C.Gray + "Timer: ";
                    for (int k = 0; k < timerChecks.size(); ++k) {
                        Check check7 = timerChecks.get(k);
                        checks2 = checks2 + (check7.isEnabled() ? C.Green : C.Red) + check7.getName() + ((timerChecks.size() - 1 == k) ? "" : (C.Gray + ", "));
                    }
                    String checks3 = C.Gray + "Silent: ";
                    for (int l = 0; l < silentChecks.size(); ++l) {
                        Check check8 = silentChecks.get(l);
                        checks3 = checks3 + (check8.isEnabled() ? C.Green : C.Red) + check8.getName() + ((silentChecks.size() - 1 == l) ? "" : (C.Gray + ", "));
                    }
                    sender.sendMessage(checks3);
                    sender.sendMessage(checks2);
                    sender.sendMessage(checks);
                    sender.sendMessage(C.Gray + C.Strike + "----------------------------------------------------");
                    return true;
                }
        	} else {
        		if(args[0].equalsIgnoreCase("status")){
                   if(sender instanceof Player) {
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
        	}
        	 if(args[0].equalsIgnoreCase("dump")){
                 String playerName = args[1];
                 String checkName = args[2];
                Check check = null;
                for (Check checkcheck : this.Daedalus.getChecks()) {
                    if (checkcheck.getIdentifier().equalsIgnoreCase(checkName)) {
                        check = checkcheck;
                    }
                }
                if (check == null) {
                    sender.sendMessage(String.valueOf(C.Red) + "This check does not exist!");
                    return true;
                }
                 String result = check.dump(playerName);
                if (result == null) {
                    sender.sendMessage(String.valueOf(C.Red) + "Error creating dump file for player " + playerName + ".");
                }
                sender.sendMessage(Daedalus.PREFIX + C.Gray + "Dropped dump thread at " + C.Yellow + "/dumps/" + result + ".txt");
                return true;
            }
        	if(args[0].equalsIgnoreCase("refresh")) {
        		sender.sendMessage(Daedalus.PREFIX + C.Red + "Resetting all data...");
        		sender.sendMessage(Daedalus.resetData());
        		return true;
        	}
        	if(args[0].equalsIgnoreCase("test")) {
        		StringBuilder reasonBuilder = new StringBuilder();
        	       for (int i = 1; i < args.length; i++) {
        	               reasonBuilder.append(args[i]).append(" ");
        	       }
        	      
        	       String string = reasonBuilder.toString();
        	       
        	    try {
					sender.sendMessage(UtilMath.decrypt(string));
				} catch (Exception e) {
					sender.sendMessage("error!");
					e.printStackTrace();
				}
        	    return true;
        	}
        	if(args[0].equalsIgnoreCase("bans")) {
        		if(sender instanceof Player) {
        			Player p = (Player) sender;
            		ChecksGUI.openBans(p);
        		} else {
        			sender.sendMessage(C.Red + "This is for players only!");
        		}
        		return true;
        	}
        	if(args[0].equalsIgnoreCase("help")) {
        	    sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
                sender.sendMessage(C.Red + C.Bold + "Daedalus Help:");
                sender.sendMessage(" ");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " help" + C.Gray + "  - View the help page.");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " dump" + C.Gray + " - Dump a check log of a player.");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " ping" + C.Gray + "  - Get your ping.");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " bans" + C.Gray + " - Lists bans this restart.");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " refresh" + C.Gray + "- Reset all Daedalus data.");
                sender.sendMessage(C.Gray + "/daedalus" + C.Reset + " status <player>" + C.Gray + " - Gets the status of Daedalus or player.");
                sender.sendMessage(C.DGray + C.Strike + "----------------------------------------------------");
                return true;
        	}
        	if(args[0].equalsIgnoreCase("resetConfig")) {
        		sender.sendMessage(Daedalus.PREFIX + C.Gray + "Resetting config...");
        		File file = new File(Daedalus.getDataFolder(), "config.yml");
        		try {
					Files.deleteIfExists(file.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
                Daedalus.getConfig().addDefault("bans", 0);
                Daedalus.getConfig().addDefault("testmode", false);
                Daedalus.getConfig().addDefault("prefix", "&8[&c&lDaedalus&8] ");
                Daedalus.getConfig().addDefault("alerts.primary", "&7");
                Daedalus.getConfig().addDefault("alerts.secondary", "&c");
                Daedalus.getConfig().addDefault("alerts.checkColor", "&b");
                Daedalus.getConfig().addDefault("bancmd", "ban %player% [Daedalus] Unfair Advantage: %check%");
                Daedalus.getConfig().addDefault("broadcastmsg", "&c&lDaedalus &7has detected &c%player% &7to be cheating and has been removed from the network.");
                Daedalus.getConfig().addDefault("settings.broadcastResetViolationsMsg", true);
                Daedalus.getConfig().addDefault("settings.violationResetTime", 60);
                Daedalus.getConfig().addDefault("settings.resetViolationsAutomatically", true);
                Daedalus.getConfig().addDefault("settings.gui.checkered", true);
                Daedalus.getConfig().addDefault("settings.latency.ping", 220);
                Daedalus.getConfig().addDefault("settings.latency.tps", 17);
                Daedalus.getConfig().addDefault("settings.sotwMode", false);
                for(Check check : Daedalus.Checks) {
                	Daedalus.getConfig().addDefault("checks." + check.getIdentifier() + ".enabled", check.isEnabled());
                	Daedalus.getConfig().addDefault("checks." + check.getIdentifier() + ".bannable", check.isBannable());
                	Daedalus.getConfig().addDefault("checks." + check.getIdentifier() + ".banTimer", check.hasBanTimer());
                }
                Daedalus.getConfig().addDefault("checks.ReachA.highChanceToBan", 3);
                Daedalus.getConfig().addDefault("checks.ReachA.chanceForBannable", 70);
                Daedalus.getConfig().addDefault("checks.ReachA.maxReach", 5.0D);
                Daedalus.getConfig().addDefault("checks.Step.TypeA.Enabled", true);
                Daedalus.getConfig().addDefault("checks.Step.TypeB.Enabled", true);
                Daedalus.getConfig().addDefault("checks.Step.TypeC.Enabled", true);
                Daedalus.getConfig().addDefault("checks.Phase.pearlFix", true);
                Daedalus.getConfig().options().copyDefaults(true);
                Daedalus.saveConfig();
                sender.sendMessage(Daedalus.PREFIX + C.Green + "Successfully reset the config.yml to its default state!");
                return true;
        	}
        	if(args[0].equalsIgnoreCase("ping")) {
        		if(sender instanceof Player) {
        			Player p = (Player) sender;
            		if(args.length == 1) {
            			sender.sendMessage(Daedalus.PREFIX + C.DGray + "[Vanilla] " + C.Gray + "Your ping: " + C.Red + Daedalus.getLag().getPing(p));
            			sender.sendMessage(Daedalus.PREFIX + C.DGray + "[Daedalus] " + C.Gray + "Your ping: " + C.Red + DaedalusAPI.getPing(p));
            			return true;
            		}
            		if(args.length == 2) {
            			Player target = Bukkit.getPlayer(args[1]);
            			if(target == null) {
            				sender.sendMessage(Daedalus.PREFIX + C.Red + "That player is not online!");
            				return true;
            			}
            			sender.sendMessage(Daedalus.PREFIX + C.White + target.getName() + "'s " + C.Gray + " ping: " + C.Red + Daedalus.getLag().getPing(target));
            			sender.sendMessage(Daedalus.PREFIX + C.White + target.getName() + "'s " + C.Gray + " ping: " + C.Red + DaedalusAPI.getPing(target));
            			return true;
            		}
            		sender.sendMessage(Daedalus.PREFIX + C.Red + "Incorrect arguments. Usage: /daedalus ping [player]");
        		} else {
        			sender.sendMessage(C.Red + "This is for players only!");
        		}
        		return true;
        	}
        	sender.sendMessage(C.Red + "Unknown argument '/" + alias + " " + args[0] + "'! Do " + C.Italics + "/daedalus help " + C.Red + "for more info!");
        	
        }
        return true;
    }
}