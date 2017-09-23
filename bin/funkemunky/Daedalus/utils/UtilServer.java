package funkemunky.Daedalus.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UtilServer
{
    public static Player[] getPlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static List<Entity> getEntities(final World world) {
    	return world.getEntities();
    }
}