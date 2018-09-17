package me.funke.daedalus.check.other;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Vape extends Check implements PluginMessageListener {

    public Vape(me.funke.daedalus.Daedalus Daedalus) {
        super("Vape", "Vape", Daedalus);

        this.setEnabled(true);
        this.setBannable(true);

        this.setMaxViolations(0);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
    }

    public void onPluginMessageReceived(String s, Player player, byte[] data) {
        String str;
        try {
            str = new String(data);
        } catch (Exception ex) {
            str = "";
        }

        getDaedalus().logCheat(this, player, "Using Cracked Vape!", Chance.HIGH, "Banned");
    }

}
