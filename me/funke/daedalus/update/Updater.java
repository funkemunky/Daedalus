package me.funke.daedalus.update;

import me.funke.daedalus.Daedalus;
import org.bukkit.Bukkit;

public class Updater implements Runnable {
    private me.funke.daedalus.Daedalus Daedalus;
    private int updater;

    public Updater(Daedalus Daedalus) {
        this.Daedalus = Daedalus;
        this.updater = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.Daedalus, this, 0, 1);
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
                    Bukkit.getPluginManager().callEvent(event);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            ++n2;
        }
    }
}
