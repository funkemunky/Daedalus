package anticheat.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import anticheat.Daedalus;
import anticheat.packets.events.PacketPlayerEvent;
import anticheat.packets.events.PacketPlayerType;

public class Latency implements Listener {

	public static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
	public static Map<UUID, Long> lastPacket;
	public List<UUID> blacklist;
	private static Map<UUID, Integer> packets;

	private Daedalus Daedalus;

	public Latency(Daedalus Daedalus) {
		this.Daedalus = Daedalus;
		this.packetTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		this.lastPacket = new HashMap<UUID, Long>();
		this.blacklist = new ArrayList<UUID>();
		this.packets = new HashMap<UUID, Integer>();
	}

	public static Integer getLag(Player player) {
		if (packets.containsKey(player.getUniqueId())) {
			return packets.get(player.getUniqueId());
		}
		return 0;
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event) {
		this.blacklist.add(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if (packetTicks.containsKey(e.getPlayer().getUniqueId())) {
			packetTicks.remove(e.getPlayer().getUniqueId());
		}
		if (lastPacket.containsKey(e.getPlayer().getUniqueId())) {
			lastPacket.remove(e.getPlayer().getUniqueId());
		}
		if (blacklist.contains(e.getPlayer().getUniqueId())) {
			blacklist.remove(e.getPlayer().getUniqueId());
		}
		if (packets.containsKey(e.getPlayer().getUniqueId())) {
			packets.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void PlayerChangedWorld(PlayerChangedWorldEvent event) {
		this.blacklist.add(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent event) {
		this.blacklist.add(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void PacketPlayer(PacketPlayerEvent event) {
		Player player = event.getPlayer();
		if (!Daedalus.isEnabled()) {
			return;
		}
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (Daedalus.getPing().getTPS() > 21.0D) {
			return;
		}
		if (event.getType() != PacketPlayerType.FLYING) {
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (Latency.packetTicks.containsKey(player.getUniqueId())) {
			Count = ((Integer) ((Map.Entry) Latency.packetTicks.get(player.getUniqueId())).getKey()).intValue();
			Time = ((Long) ((Map.Entry) Latency.packetTicks.get(player.getUniqueId())).getValue()).longValue();
		}
		if (Latency.lastPacket.containsKey(player.getUniqueId())) {
			long MS = System.currentTimeMillis() - Latency.lastPacket.get(player.getUniqueId()).longValue();
			if (MS >= 100L) {
				this.blacklist.add(player.getUniqueId());
			} else if ((MS > 1L) && (this.blacklist.contains(player.getUniqueId()))) {
				this.blacklist.remove(player.getUniqueId());
			}
		}
		if (!this.blacklist.contains(player.getUniqueId())) {
			Count++;
			if ((Latency.packetTicks.containsKey(player.getUniqueId())) && (TimerUtils.elapsed(Time, 1000L))) {
				packets.put(player.getUniqueId(), Count);
				Count = 0;
				Time = TimerUtils.nowlong();
			}
		}
		Latency.packetTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
		Latency.lastPacket.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
	}

}