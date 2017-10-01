package anticheat.checks.movement;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PlayerMoveEvent.class, EntityDamageByEntityEvent.class, PlayerQuitEvent.class})
public class SpeedA extends Checks {
	
	public SpeedA() {
		super("Speed", "Speed", ChecksType.MOVEMENT, 15, Daedalus.getAC(), true);
	}

	 public TimerUtils t = new TimerUtils();
	    public Location location;
	    private int groundTicks;
	    private int IceTicks;
	    private int vla = 0;
	    private int vlb = 0;
	    private int vlc = 0;
	    private int vld = 0;
	    private int vle = 0;

	    @SuppressWarnings("deprecation")
	    @Override
	    protected void onEvent(Event event) {
	        if (this.getState() == false)
	            return;

	        if (event instanceof PlayerMoveEvent) {

	            Location from = ((PlayerMoveEvent) event).getFrom().clone();
	            Location to = ((PlayerMoveEvent) event).getTo().clone();
	            PlayerMoveEvent e = (PlayerMoveEvent) event;
	            Player p = e.getPlayer();

	            Location l = p.getLocation();
	            int x = l.getBlockX();
	            int y = l.getBlockY();
	            int z = l.getBlockZ();
	            Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
	            Location loc = new Location(p.getWorld(), x, y, z);
	            Location loc2 = new Location(p.getWorld(), x, y + 1, z);
	            Location above = new Location(p.getWorld(), x, y + 2, z);
	            Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
	            if (p.getNoDamageTicks() > 3) {
	                return;
	            }

	            if (p.getVehicle() != null) {
	                return;
	            }

	            if (p.getGameMode().equals(GameMode.CREATIVE)) {
	                return;
	            }

	            if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
	                    && (e.getTo().getY() == e.getFrom().getY())) {
	                return;
	            }

	            if (IceTicks < 0) {
	                IceTicks = 0;
	            }
	            if (blockLoc.getBlock().getType() == Material.ICE || blockLoc.getBlock().getType() == Material.PACKED_ICE) {
	                ++IceTicks;
	            } else {
	                --IceTicks;
	            }

	            double Airmaxspeed = 0.36;
	            double maxSpeed = 0.415;
	            double newmaxspeed = 0.73;
	            if (IceTicks >= 100) {
	                newmaxspeed = 1.0;
	            }
	            double ig = 0.28;
	            double speed = PlayerUtils.offset(getHV(to.toVector()), getHV(from.toVector()));
	            double ongroundDiff = (to.getY() - from.getY());

	            if (p.isOnGround()) {
	                ++groundTicks;
	            } else {
	                groundTicks = 0;
	            }

	            if (p.hasPotionEffect(PotionEffectType.SPEED)) {
	                int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
	                if (level > 0) {
	                    newmaxspeed = (newmaxspeed * (((level * 20) * 0.011) + 1));
	                    Airmaxspeed = (Airmaxspeed * (((level * 20) * 0.011) + 1));
	                    maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
	                    ig = (ig * (((level * 20) * 0.011) + 1));
	                }
	            }
	            /** MOTION Y RELEATED HACKS **/
	            if (PlayerUtils.isReallyOnground(p) && !p.hasPotionEffect(PotionEffectType.JUMP)
	                    && above.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR
	                    && ongroundDiff > 0 && ongroundDiff != 0 && ongroundDiff != 0.41999998688697815
	                    && ongroundDiff != 0.33319999363422426 && ongroundDiff != 0.1568672884460831
	                    && ongroundDiff != 0.4044491418477924 && ongroundDiff != 0.4044449141847757
	                    && ongroundDiff != 0.40444491418477746 && ongroundDiff != 0.24813599859094637
	                    && ongroundDiff != 0.1647732812606676 && ongroundDiff != 0.24006865856430082
	                    && ongroundDiff != 0.20000004768370516 && ongroundDiff != 0.19123230896968835
	                    && ongroundDiff != 0.10900766491188207 && ongroundDiff != 0.20000004768371227
	                    && ongroundDiff != 0.40444491418477924 && ongroundDiff != 0.0030162615090425504
	                    && ongroundDiff != 0.05999999821186108 && ongroundDiff != 0.05199999886751172
	                    && ongroundDiff != 0.06159999881982792 && ongroundDiff != 0.06927999889612124
	                    && ongroundDiff != 0.07542399904870933 && ongroundDiff != 0.07532994414328797
	                    && ongroundDiff != 0.08033919924402255 && ongroundDiff != 0.5 && ongroundDiff != 0.08427135945886555
	                    && ongroundDiff != 0.340000110268593 && ongroundDiff != 0.30000001192092896
	                    && ongroundDiff != 0.3955758986732967 && ongroundDiff != 0.019999999105930755
	                    && ongroundDiff != 0.21560001587867816 && ongroundDiff != 0.13283301814746876
	                    && ongroundDiff != 0.05193025879327907 && ongroundDiff != 0.1875 && ongroundDiff != 0.375
	                    && ongroundDiff != 0.08307781780646728 && ongroundDiff != 0.125 && ongroundDiff != 0.25
	                    && ongroundDiff != 0.01250004768371582 && ongroundDiff != 0.1176000022888175
	                    && ongroundDiff != 0.0625 && ongroundDiff != 0.20000004768371582
	                    && ongroundDiff != 0.4044448882341385 && ongroundDiff != 0.40444491418477835) {
	                ++vla;
	                if (vla >= 5) {
	                    Alert(p, "Type A");
	                    vla = 0;
	                    kick(p);
	                }
	            }

	            /** ONGROUND SPEEDS **/
	            if (PlayerUtils.isReallyOnground(p) && to.getY() == from.getY()) {
	                if (speed >= maxSpeed && groundTicks > 20 && p.getFallDistance() < 0.15
	                        && blockLoc.getBlock().getType() != Material.ICE
	                        && blockLoc.getBlock().getType() != Material.PACKED_ICE
	                        && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
	                        && above3.getBlock().getType() == Material.AIR) {
	                    ++vlb;
	                    if (vlb >= 10) {
	                        Alert(p, "Type B");
	                        vlb = 0;
	                        kick(p);
	                        groundTicks = 0;
	                    }
	                }
	            }

	            /** MIDAIR MODIFIED SPEEDS **/
	            if (!PlayerUtils.isReallyOnground(p) && speed >= Airmaxspeed && IceTicks < 10
	                    && blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
	                    && !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
	                    && above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
	                    && blockLoc.getBlock().getType() != Material.AIR) {
	                ++vlc;
	                if (vlc >= 10) {
	                    Alert(p, "Type C");
	                    vlc = 0;
	                    kick(p);
	                }
	            }
	            /** GOING ABOVE THE SPEED LIMIT **/
	            if (speed >= newmaxspeed && IceTicks < 10 && p.getFallDistance() < 0.6
	                    && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
	                    && loc2.getBlock().getType() == Material.AIR) {
	                ++vld;
	                if (vld >= 10) {
	                    Alert(p, "Type D");
	                    vld = 0;
	                    kick(p);
	                }
	            }
	            /** Vanilla speeds check **/
	            if (speed > ig && !PlayerUtils.isAir(p) && ongroundDiff <= -0.4 && p.getFallDistance() <= 0.4
	                    && !PlayerUtils.flaggyStuffNear(p.getLocation()) && blockLoc.getBlock().getType() != Material.ICE
	                    && e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
	                    && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
	                    && above3.getBlock().getType() == Material.AIR) {
	                ++vle;
	                if (vle >= 10) {
	                    Alert(p, "Type E");
	                    vle = 0;
	                    kick(p);
	                }
	            }
	        }
	    }

	    private int getPotionEffectLevel(Player p, PotionEffectType pet) {
	        for (PotionEffect pe : p.getActivePotionEffects()) {
	            if (pe.getType().getName().equals(pet.getName())) {
	                return pe.getAmplifier() + 1;
	            }
	        }
	        return 0;
	    }

	    private Vector getHV(Vector V) {
	        V.setY(0);
	        return V;
	    }

}
