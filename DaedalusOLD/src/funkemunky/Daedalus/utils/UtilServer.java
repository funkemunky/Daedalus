package funkemunky.Daedalus.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UtilServer {
	public static ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<Player>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			list.add(player);
		}
		return list;
	}

	public static List<Entity> getEntities(final World world) {
		return world.getEntities();
	}
}