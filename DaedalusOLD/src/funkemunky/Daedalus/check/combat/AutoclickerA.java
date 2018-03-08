package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.packets.events.PacketSwingArmEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class AutoclickerA extends Check {
	
	public Map<UUID, Integer> clicks;
	private Map<UUID, Long> recording;

	public AutoclickerA(Daedalus Daedalus) {
		super("AutoclickerA", "Autoclicker (Type A)", Daedalus);

		setEnabled(true);
		setBannable(true);
		setViolationsToNotify(1);
		setMaxViolations(5);
		
		clicks = new HashMap<UUID, Integer>();
		recording = new HashMap<UUID, Long>();
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (clicks.containsKey(uuid)) {
			clicks.remove(uuid);
		}
		if(recording.containsKey(uuid)) {
			recording.remove(uuid);
		}
	}

	@EventHandler
	public void onSwing(PacketSwingArmEvent e) {
		Player player = e.getPlayer();

		if (getDaedalus().isSotwMode()
				|| getDaedalus().getLag().getTPS() < 17
				|| player.hasPermission("daedalus.bypass")
				|| Latency.getLag(player) > 100) {
			return;
		}
		
		int clicks = this.clicks.getOrDefault(this, 0);
		long time = recording.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
		if(UtilTime.elapsed(time, 1000L)) {
			if(clicks > 30) {
				getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[] {clicks + " Clicks/Second"});
			}
			clicks = 0;
			recording.remove(player.getUniqueId());
		} else {
			clicks++;
		}
		
		this.clicks.put(player.getUniqueId(), clicks);
		recording.put(player.getUniqueId(), time);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent e) {
		if(clicks.containsKey(e.getPlayer().getUniqueId())) {
			clicks.remove(e.getPlayer().getUniqueId());
		}
		if(recording.containsKey(e.getPlayer().getUniqueId())) {
			recording.remove(e.getPlayer().getUniqueId());
		}
	}
}
