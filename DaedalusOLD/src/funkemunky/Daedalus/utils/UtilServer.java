package funkemunky.Daedalus.utils;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class UtilServer
{
	public static Integer getOnlinePlayers() {
		try {
	    	if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
	            return ((Collection<?>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0])).size();
	        else
	            return ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0])).length;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
    public static List<Entity> getEntities(final World world) {
    	return world.getEntities();
    }
}