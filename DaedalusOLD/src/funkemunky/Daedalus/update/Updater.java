package funkemunky.Daedalus.update;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import funkemunky.Daedalus.Daedalus;

public class Updater implements Runnable {
	private Daedalus Daedalus;
	private int updater;

	public Updater(Daedalus Daedalus) {
		this.Daedalus = Daedalus;
		this.updater = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) this.Daedalus, (Runnable) this, 0, 1);
	}

	public void Disable() {
		Bukkit.getScheduler().cancelTask(this.updater);
	}

	@Override
	public void run() {
		UpdateType[] arrupdateType = UpdateType.values();
		int n = arrupdateType.length;
		int n2 = 0;
		while (n2 < n) {
			UpdateType updateType = arrupdateType[n2];
			if (updateType != null && updateType.Elapsed()) {
				try {
					UpdateEvent event = new UpdateEvent(updateType);
					Bukkit.getPluginManager().callEvent((Event) event);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			++n2;
		}
	}
}
