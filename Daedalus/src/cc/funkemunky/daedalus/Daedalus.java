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
        //This allows us to access this class's contents from others places.
        instance = this;

        //Starting up our utilities, managers, and tasks.
        dataManager = new DataManager();
        runTasks();

        //Registering all the listeners to Bukkit's event handler.
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CancelEvents(), this);
        Bukkit.getPluginManager().registerEvents(new BukkitListeners(), this);

        //Register all the Atlas listeners to Atlas's event handler.
        EventManager.register(new PacketListeners());
    }

    private void runTasks() {
        //This allows us to use ticks for time comparisons to allow for more parrallel calculations to actual Minecraft
        //and it also has the added benefit of being lighter than using System.currentTimeMillis.
        new BukkitRunnable() {
            public void run() {
                currentTicks++;
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
