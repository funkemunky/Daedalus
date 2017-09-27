package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketKillauraEvent;
import funkemunky.Daedalus.packets.events.PacketPlayerType;
import funkemunky.Daedalus.utils.Chance;

public class KillAuraD extends Check {
	
	public static Map<UUID, Map.Entry<Double, Double>> packetTicks;
	
	public KillAuraD(Daedalus Daedalus) {
		super("KillAuraD", "KillAura (Packet)", Daedalus);
		
		this.setEnabled(true);
		this.setBannable(false);
		
		this.setMaxViolations(5);
		this.setViolationResetTime(60000);
		
		this.packetTicks = new HashMap<UUID, Map.Entry<Double, Double>>();
		
		new BukkitRunnable() {
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(packetTicks.containsKey(player.getUniqueId())) {
						packetTicks.remove(player.getUniqueId());
					}
				}
			}
		}.runTaskTimer(Daedalus, 20L, 20L);
	}

	
	@EventHandler
	public void packet(PacketKillauraEvent e) {
		if(!getDaedalus().isEnabled()) {
			return;
		}
		if(e.getPlayer().hasPermission("daedalus.bypass")) {
			return;
		}
		double Count = 0;
		double Other = 0;
		if(packetTicks.containsKey(e.getPlayer().getUniqueId())) {
			Count = packetTicks.get(e.getPlayer().getUniqueId()).getKey();
			Other = packetTicks.get(e.getPlayer().getUniqueId()).getValue();
		}
		
		if(e.getType() == PacketPlayerType.ARM_SWING) {
			Other++;
		}
		
		if(e.getType() == PacketPlayerType.USE) {
			Count++;
		}
		
		if(Count > Other + 1) {
			getDaedalus().logCheat(this, e.getPlayer(), Count + " Use : " + Other + " Arm", Chance.HIGH, new String[0]);
		}
		
		this.packetTicks.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<Double, Double>(Count, Other));
	}

}
