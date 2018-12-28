package cc.funkemunky.daedalus.api.data;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataManager {
    private List<PlayerData> dataObjects = Lists.newArrayList();

    public DataManager() {
        Bukkit.getOnlinePlayers().forEach(player -> addData(player.getUniqueId()));
    }

    public PlayerData getPlayerData(UUID uuid) {
        Optional<PlayerData> opData = dataObjects.stream().filter(data -> data.getUuid().equals(uuid)).findFirst();

        if(opData.isPresent()) {
            return opData.get();
        } else {
            PlayerData data = new PlayerData(uuid);
            dataObjects.add(data);
            return data;
        }
    }

    public void addData(UUID uuid) {
        dataObjects.add(new PlayerData(uuid));
    }

    public void removeData(UUID uuid) {
        dataObjects.stream().filter(data -> data.getUuid().equals(uuid)).forEach(data -> dataObjects.remove(data));
    }
}
