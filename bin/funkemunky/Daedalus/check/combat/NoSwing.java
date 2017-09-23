package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketSwingArmEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class NoSwing
        extends Check
{
    public NoSwing(Daedalus Daedalus)
    {
        super("NoSwing", "NoSwing", Daedalus);

        this.setBannable(true);
    }

    public static Map<UUID, Long> LastArmSwing = new HashMap();

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e)
    {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        if (getDaedalus().getLag().getTPS() < 17.0D) {
            return;
        }
        Player player = (Player)e.getDamager();
        
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }

        Player fplayer = player;
        if (getDaedalus().isEnabled()) {
            new BukkitRunnable() {
            	public void run() {
                    if (!NoSwing.this.hasSwung(fplayer, Long.valueOf(1500L))) {
                        NoSwing.this.getDaedalus().logCheat(NoSwing.this, fplayer, null, Chance.HIGH, new String[0]);
                    }
            	}
            }.runTaskLater(getDaedalus(), 10L);
        }
    }

    public boolean hasSwung(Player player, Long time)
    {
        if (!this.LastArmSwing.containsKey(player.getUniqueId())) {
            return false;
        }
        return UtilTime.nowlong() < ((Long)this.LastArmSwing.get(player.getUniqueId())).longValue() + time.longValue();
    }

    @EventHandler
    public void ArmSwing(PacketSwingArmEvent event)
    {
        this.LastArmSwing.put(event.getPlayer().getUniqueId(), Long.valueOf(UtilTime.nowlong()));
    }
}
