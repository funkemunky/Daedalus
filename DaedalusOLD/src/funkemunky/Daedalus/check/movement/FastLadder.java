package funkemunky.Daedalus.check.movement;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class FastLadder extends Check {
	
	public Map<Player, Integer> count;

	public FastLadder(Daedalus Daedalus) {
		super("FastLadder", "FastLadder", Daedalus);

		this.setEnabled(true);
		this.setBannable(true);
		this.setMaxViolations(7);
		
		//A WeakHashMap removes an index if Player returns null.
		count = new WeakHashMap<Player, Integer>();
	}

	@EventHandler
	public void checkFastLadder(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		/** False flag check **/
		if(e.isCancelled()
				|| (e.getFrom().getY() == e.getTo().getY())
				|| getDaedalus().isSotwMode()
				|| player.getAllowFlight()
				|| getDaedalus().getLastVelocity().containsKey(player.getUniqueId())
				|| player.hasPermission("daedalus.bypass")
				|| !UtilPlayer.isOnClimbable(player, 1) || 
				!UtilPlayer.isOnClimbable(player, 0)) {
			return;
		}

		int Count = count.getOrDefault(player, 0);
		double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()),
				UtilMath.getVerticalVector(e.getTo().toVector()));
		double Limit = 0.13;
		
		double updown = e.getTo().getY() - e.getFrom().getY();
		if (updown <= 0) {
			return;
		}

		
		/** Checks if Y Delta is greater than Limit **/
		
		if (OffsetY > Limit) {
			Count++;
			this.dumplog(player, "[Illegitmate] New Count: " + Count + " (+1); Speed: " + OffsetY + "; Max: " + Limit);
		} else {
			Count = Count > -2 ? Count - 1 : 0;
		}

		long percent = Math.round((OffsetY - Limit) * 120);
		
		/**If verbose count is greater than 11, flag **/
		if (Count > 11) {
			Count = 0;
			this.dumplog(player,
					"Flagged for FastLadder; Speed:" + OffsetY + "; Max: " + Limit + "; New Count: " + Count);
			this.getDaedalus().logCheat(this, player, percent + "% faster than normal", Chance.HIGH, new String[0]);
		}
		count.put(player, Count);
	}

}
