package funkemunky.Daedalus.check.other;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketPlayerEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class Timer extends Check {
	private Map<UUID, Map.Entry<Integer, Long>> packets;
	private Map<UUID, Integer> verbose;
	private Map<UUID, Long> lastPacket;
	private List<Player> toCancel;

	public Timer(Daedalus Daedalus) {
		super("TimerA", "Timer (Type A)", Daedalus);
		
		packets = new HashMap<UUID, Map.Entry<Integer, Long>>();
		verbose = new HashMap<UUID, Integer>();
		toCancel = new ArrayList<Player>();
		lastPacket = new HashMap<UUID, Long>();

		setEnabled(true);
		setBannable(false);
		setMaxViolations(5);
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if(packets.containsKey(e.getPlayer().getUniqueId())) {
			packets.remove(e.getPlayer().getUniqueId());
		}
		if(verbose.containsKey(e.getPlayer().getUniqueId())) {
			verbose.remove(e.getPlayer().getUniqueId());
		}
		if(lastPacket.containsKey(e.getPlayer().getUniqueId())) {
			lastPacket.remove(e.getPlayer().getUniqueId());
		}
		if(toCancel.contains(e.getPlayer())) {
			toCancel.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void PacketPlayer(PacketPlayerEvent event) {
		Player player = event.getPlayer();
		if (!this.getDaedalus().isEnabled()) {
			return;
		}

		if (player.hasPermission("daedalus.bypass")) {
			return;
		}

		if (getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
			return;
		}
		
		long lastPacket = this.lastPacket.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
		int packets = 0;
		long Time = System.currentTimeMillis();
		int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);
		
		if (this.packets.containsKey(player.getUniqueId())) {
			packets = this.packets.get(player.getUniqueId()).getKey().intValue();
			Time = this.packets.get(player.getUniqueId()).getValue().longValue();
		}
		
		if((System.currentTimeMillis() - lastPacket) > 100L) {
			toCancel.add(player);
		}
		double threshold = 23;
		if(UtilTime.elapsed(Time, 1000L)) {
			if(toCancel.remove(player) && packets <= 13) {
				return;
			}
			if(packets > threshold + getDaedalus().packet.movePackets.getOrDefault(player.getUniqueId(), 0) && getDaedalus().packet.movePackets.getOrDefault(player.getUniqueId(), 0) < 5) {
				verbose = (packets - threshold) > 10 ? verbose + 2 : verbose + 1;
			} else {
				verbose = 0;
			}
			
			if(verbose > 2) {
				getDaedalus().logCheat(this, player, "Packets: " + packets, Chance.HIGH, new String[0]);
			}
			packets = 0;
			Time = UtilTime.nowlong();
			getDaedalus().packet.movePackets.remove(player.getUniqueId());
		}
		packets++;
		
        this.lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
		this.packets.put(player.getUniqueId(), new SimpleEntry<Integer, Long>(packets, Time));
		this.verbose.put(player.getUniqueId(), verbose);
	}
}