package cc.funkemunky.daedalus.api.data;

import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.impl.checks.Fly;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {
    private UUID uuid;
    private List<Check> checks = Lists.newArrayList();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        loadChecks();
    }

    private void registerCheck(Check check) {
        Bukkit.getPluginManager().registerEvents(check, Daedalus.getInstance());
        EventManager.register(check);

        checks.add(check);
    }

    private void loadChecks() {
        registerCheck(new Fly("Fly", CancelType.MOTION, this));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
