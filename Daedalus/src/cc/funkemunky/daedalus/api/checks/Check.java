package cc.funkemunky.daedalus.api.checks;

import cc.funkemunky.api.event.system.Listener;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.daedalus.api.data.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@Getter
@Setter
public abstract class Check implements Listener, org.bukkit.event.Listener {
    private String name;
    private CancelType cancelType;
    private PlayerData data;
    private int vl;

    public Check(String name, CancelType cancelType, PlayerData data) {
        this.name = name;
        this.cancelType = cancelType;
        this.data = data;
        vl = 0;
    }

    protected void flag(String information, boolean cancel) {
        vl++;

        if(cancel) data.setCancelType(cancelType);

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("daedalus.alerts")).forEach(player -> player.sendMessage(Color.translate("&8[&4&lDaedalus&8] &c" + data.getPlayer().getName() + " &7has failed &c" + getName() + " &8(&c" + vl + "&8) &8[&7&o" + information + "&8]")));
    }

    public abstract void onPacket(Object packet, String packetType);

    public abstract void onBukkitEvent(Event event);
}
