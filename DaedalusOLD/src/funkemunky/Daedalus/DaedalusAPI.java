package funkemunky.Daedalus;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.C;
import funkemunky.Daedalus.utils.Chance;

public class DaedalusAPI {

	private static Daedalus daedalus;

	@SuppressWarnings("unused")
	private Plugin plugin;

	public DaedalusAPI(Plugin plugin) {
		this.plugin = plugin;
		daedalus = (Daedalus) plugin;
	}

	public static List<Check> getChecks() {
		return daedalus.getChecks();
	}

	public static Integer getPing(Player player) {
		return Integer.valueOf(Math.round((Latency.getLag(player) / 2) * 6));
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
