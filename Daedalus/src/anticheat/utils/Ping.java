package anticheat.utils;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.EntityPlayer;


/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Ping {

	public int getPing(Player player) {
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		return ep.ping;
	}

}
