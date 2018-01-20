package funkemunky.Daedalus.check.other;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;

public class Vape extends Check implements PluginMessageListener {

	public Vape(Daedalus Daedalus) {
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

		getDaedalus().logCheat(this, player, "Using Cracked Vape!", Chance.HIGH, new String[] { "Banned" });
	}

}
