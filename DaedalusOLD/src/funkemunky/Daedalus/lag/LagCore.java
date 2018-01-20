package funkemunky.Daedalus.lag;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.Daedalus;

public class LagCore implements Listener {
	public Daedalus Daedalus;
	private double tps;

	public LagCore(Daedalus Daedalus) {
		this.Daedalus = Daedalus;

		new BukkitRunnable() {
			long sec;
			long currentSec;
			int ticks;

			public void run() {
				this.sec = (System.currentTimeMillis() / 1000L);
				if (this.currentSec == this.sec) {
					this.ticks += 1;
				} else {
					this.currentSec = this.sec;
					LagCore.this.tps = (LagCore.this.tps == 0.0D ? this.ticks : (LagCore.this.tps + this.ticks) / 2.0D);
					this.ticks = 0;
				}
			}
		}.runTaskTimerAsynchronously(Daedalus, 1L, 1L);

		this.Daedalus.RegisterListener(this);
	}

	public double getTPS() {
		return this.tps + 1.0D > 20.0D ? 20.0D : this.tps + 1.0D;
	}

	public double getLag() {
		return Math.round((1.0D - tps / 20.0D) * 100.0D);
	}

	public double getFreeRam() {
		return Math.round(Runtime.getRuntime().freeMemory() / 1000000);
	}

	public double getMaxRam() {
		return Math.round(Runtime.getRuntime().maxMemory() / 1000000);
	}

	public static Object getNmsPlayer(Player p) throws Exception {
		Method getHandle = p.getClass().getMethod("getHandle");
		return getHandle.invoke(p);
	}

	public static Object getFieldValue(Object instance, String fieldName) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	public int getPing(Player who) {
		try {
			// Building the version of the server in such a form we can use it
			// in NMS code.
			String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
			// Getting craftplayer
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			// Invoking method getHandle() for the player
			Object handle = craftPlayer.getMethod("getHandle").invoke(who);
			// Getting field "ping" that holds player's ping obviously
			Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);
			// Returning the ping
			return ping.intValue();
		} catch (Exception e) {
			// Handle exceptions however you like, i chose to return value of
			// -1; since player's ping can't be -1.
			return 404;
		}
	}
}