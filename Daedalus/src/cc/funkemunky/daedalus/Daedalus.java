package cc.funkemunky.daedalus;

import cc.funkemunky.daedalus.api.data.DataManager;
import cc.funkemunky.daedalus.impl.listeners.PlayerConnectionListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Daedalus extends JavaPlugin {
    @Getter
    private static Daedalus instance;
    private DataManager dataManager;
    public void onEnable() {
        instance = this;

        dataManager = new DataManager();

        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListeners(), this);
    }
}
