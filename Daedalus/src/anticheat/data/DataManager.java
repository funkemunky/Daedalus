package anticheat.data;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class DataManager {

	private HashMap<Player, Data> profiles = new LinkedHashMap<>();

	public Data getProfil(Player p) {
		return profiles.get(p);
	}

	public void loadProfil(Player p) {
		profiles.put(p, new Data(p));
	}

	public void saveProfil(Player p) {
		profiles.remove(p);
	}

	public void addDetecton(Player p, Checks detection) {
		Data profile = getProfil(p);
		if (profile != null) {
			Data.addDetection(detection);
		}
	}

	public void loaddata() {
		Player[] players = Daedalus.getAC().getServer().getOnlinePlayers();
		for (Player p : players) {
			loadProfil(p);
		}
	}

}
