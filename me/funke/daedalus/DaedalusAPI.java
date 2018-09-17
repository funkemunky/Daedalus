package me.funke.daedalus;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.check.other.Latency;
import me.funke.daedalus.utils.C;
import me.funke.daedalus.utils.Chance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DaedalusAPI {
    private static Daedalus daedalus;
    private Plugin plugin;

    public DaedalusAPI(Plugin plugin) {
        this.plugin = plugin;
        daedalus = (Daedalus) plugin;
    }

    public static List<Check> getChecks() {
        return daedalus.getChecks();
    }

    public static Integer getPing(Player player) {
        return Math.round((Latency.getLag(player) / 2) * 6);
    }

    public static String getChanceString(Chance chance) {
        if (chance == Chance.HIGH) {
            return C.Red + "HIGH";
        }
        if (chance == Chance.LIKELY) {
            return C.Gold + "LIKELY";
        }
        return C.Gray + "UNKNOWN";
    }
}