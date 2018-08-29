package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import funkemunky.Daedalus.Daedalus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilTime;

public class AscensionB extends Check {

	public AscensionB(funkemunky.Daedalus.Daedalus Daedalus) {
		super("AscensionB", "Ascension (Type B)", Daedalus);

		setBannable(true);
		setEnabled(true);
		setMaxViolations(5);
	}

	private Map<Player, Integer> verbose = new WeakHashMap<>();
	private Map<Player, Float> lastYMovement = new WeakHashMap<>();

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		int verbose = this.verbose.getOrDefault(player, 0);
		float yDelta = (float) (e.getTo().getY() - e.getFrom().getY());

		if(lastYMovement.containsKey(player)
				&& Math.abs(yDelta - lastYMovement.get(player)) < 0.002) {
			if(verbose++ > 5) {
				Daedalus.Instance.logCheat(this, player, Math.abs(yDelta - lastYMovement.get(player)) + "<-" + 0.002, Chance.HIGH);
			}
		}

		lastYMovement.put(player, yDelta);
		this.verbose.put(player, verbose);
	}

}
