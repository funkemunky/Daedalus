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
import funkemunky.Daedalus.packets.events.PacketKillauraEvent;
import funkemunky.Daedalus.packets.events.PacketPlayerType;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;

public class KillauraG extends Check {
	
	private Map<UUID, Integer> verbose;
	private Map<UUID, Long> lastArmSwing;
	
	public KillauraG(Daedalus Daedalus) {
		super("KillauraG", "Killaura (Type G)", Daedalus);
		
		setEnabled(true);
		setBannable(false);
		
		verbose = new HashMap<UUID, Integer>();
		lastArmSwing = new HashMap<UUID, Long>();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		if(verbose.containsKey(uuid)) {
			verbose.remove(uuid);
		}
		if(lastArmSwing.containsKey(uuid)) {
			lastArmSwing.containsKey(uuid);
		}
	}
	
	@EventHandler
	public void onHit(PacketUseEntityEvent e) { 
		
		if(!getDaedalus().isEnabled()) {
			return;
		}
		
		Player player = (Player) e.getAttacker();
		
		int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);
		
		if(player.isDead()) {
			verbose++;
		} else if(this.verbose.containsKey(player.getUniqueId())) {
			this.verbose.remove(player.getUniqueId());
			return;
		}
		
		if(verbose > 4) {
			verbose = 0;
			getDaedalus().logCheat(this, player, "Hit another player while dead.", Chance.HIGH, new String[0]);
		}
		
		this.verbose.put(player.getUniqueId(), verbose);
	}
	
	@EventHandler
	public void onSwing(PacketKillauraEvent e) {
		if(!getDaedalus().isEnabled()
				|| getDaedalus().getLag().getTPS() < 19) {
			return;
		}
		
		Player player = e.getPlayer();
		if(e.getType() == PacketPlayerType.ARM_SWING) {
		    lastArmSwing.put(player.getUniqueId(), System.currentTimeMillis());
		}
		
		if(e.getType() == PacketPlayerType.USE) {
			long lastArmSwing = this.lastArmSwing.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
			
			if((System.currentTimeMillis() - lastArmSwing) > 100 && Latency.getLag(player) < 50) {
				getDaedalus().logCheat(this, player, "Missed while looking at player", Chance.LIKELY, new String[0]);
			}
		}
	}
	
	

}
