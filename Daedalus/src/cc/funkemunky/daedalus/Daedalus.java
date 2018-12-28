package cc.funkemunky.daedalus;

import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.daedalus.api.data.DataManager;
import cc.funkemunky.daedalus.impl.listeners.BukkitListeners;
import cc.funkemunky.daedalus.impl.listeners.CancelEvents;
import cc.funkemunky.daedalus.impl.listeners.PacketListeners;
import cc.funkemunky.daedalus.impl.listeners.PlayerConnectionListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Daedalus extends JavaPlugin {
    @Getter
    private static Daedalus instance;
    private DataManager dataManager;
    private int currentTicks;

    public void onEnable() {
        instance = this;

        dataManager = new DataManager();
        runTasks();
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CancelEvents(), this);
        Bukkit.getPluginManager().registerEvents(new BukkitListeners(), this);
        EventManager.register(new PacketListeners());
    }

    private void runTasks() {
        new BukkitRunnable() {
            public void run() {
                currentTicks++;
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
