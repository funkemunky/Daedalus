package anticheat.checks;

import org.bukkit.event.entity.*;

import anticheat.utils.*;
import anticheat.detections.*;
import anticheat.user.User;
import anticheat.*;

import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.event.*;

import org.bukkit.*;

@ChecksListener(events = { EntityDamageByEntityEvent.class })
public class Reach extends Checks {

	private Ping ping = new Ping();

	public Reach() {
		super("Reach", ChecksType.COMBAT, Daedalus.getAC(), true);
	}

	/**
	 * Returns the player potion effect level.
	 */

	private int getPotionEffectLevel(Player p, PotionEffectType pet) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
	}

	@Override
	protected void onEvent(Event event) {
		/**
		 * Checks if the check is enabled, if not return.
		 */
		if (!this.getState()) {
			return;
		}
		if (event instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			Player damager = (Player) e.getDamager();

			Player damaged = (Player) e.getEntity();

			/**
			 * If the damager is in gamemode creative return, since creative
			 * reach is higher then survival reach.
			 */

			if (damager.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			/**
			 * Checks if the damager and the damage player are players (if they
			 * are not mobs or something else...)
			 */
			if (!(damager instanceof Player)) {
				return;
			}
			if (!(damaged instanceof Player)) {
				return;
			}
			/**
			 * Gets the velocity length of the damaged entity.
			 */
			double velocity = damaged.getVelocity().length();
			/**
			 * The maximum range player can get, 3.0 is the default reach, plus
			 * the velocity multiplied by 4 (in combos).
			 */
			double rangeThreshold = 3.0f + (velocity * 4.0);
			/**
			 * The distance between the damager and the damaged entity.
			 */
			double range = damager.getLocation().distance(damaged.getLocation());

			/**
			 * Checks if the damager have a speed effect.
			 */
			if (damager.hasPotionEffect(PotionEffectType.SPEED)) {
				int level = this.getPotionEffectLevel(damager, PotionEffectType.SPEED);
				if (level == 1) {
					/**
					 * Increase the rangeThreshold by 0.1 if the speed level is
					 * 1.
					 */
					rangeThreshold += 0.1;
				} else {
					/**
					 * Increase the rangeThreshold by 0.2 for all other values,
					 * in mc speed level can be either 1 or 2 if you use pots.
					 */
					rangeThreshold += 0.2;
				}
			}

			/**
			 * If the damaged person velocity length is grather then 0.2 add 0.2
			 * to the rangeThreshold.
			 */

			if (damaged.getVelocity().length() >= 0.2) {
				rangeThreshold += 0.2;
			}

			/**
			 * If the damaged player is sprinting (Not walking backwards) or if
			 * the damager is 1.7ing the damaged player increase the
			 * rangeThreshold by 2.
			 */

			if ((!damaged.isSprinting()) || PlayerUtils.getDistanceBetweenAngles(damager.getLocation().getYaw(),
					damaged.getLocation().getYaw()) < 90) {
				rangeThreshold += 2;
			}

			/**
			 * Increases the rangeThreshold depending on the player ping to
			 * avoid few false flags.
			 */

			if (this.ping.getPing(damager) <= 50) {
				rangeThreshold += 0.1;
			} else if (this.ping.getPing(damager) > 50 && this.ping.getPing(damager) < 100) {
				rangeThreshold += 0.2;
			} else if (this.ping.getPing(damager) >= 100 && this.ping.getPing(damager) < 150) {
				rangeThreshold += 0.3;
			} else if (this.ping.getPing(damager) >= 150 && this.ping.getPing(damager) < 200) {
				rangeThreshold += 0.4;
			} else if (this.ping.getPing(damager) >= 200 && this.ping.getPing(damager) < 250) {
				rangeThreshold += 0.5;
			} else if (this.ping.getPing(damager) >= 250 && this.ping.getPing(damager) < 300) {
				rangeThreshold += 0.6;
			} else if (this.ping.getPing(damager) >= 300 && this.ping.getPing(damager) < 350) {
				rangeThreshold += 0.7;
			} else if (this.ping.getPing(damager) >= 350 && this.ping.getPing(damager) < 400) {
				rangeThreshold += 0.8;
			} else {
				rangeThreshold += 2.0;
			}

			/**
			 * To get the damager profile.
			 */
			User user = Daedalus.getUserManager().getUser(damager.getUniqueId());

			/**
			 * Get the damager violations.
			 */

			int vl = user.getVL();
			if (range >= rangeThreshold) {
				/**
				 * If the range is grater then the rangeThreshold increase the
				 * violation.
				 */
				user.setVL(vl + 1);
				/**
				 * If the violations is above or equal to 5 violations starts
				 * alerting staff and if he needs a ban, ban him.
				 */
				if (vl >= 5) {
					Alert(damager, "§a" + damager.getName() + " §b" + ping.getPing(damager) + " §5:" + "§a"
							+ damaged.getName() + " §b" + ping.getPing(damaged) + " §4VL: §a" + user.getVL());
					kick(damager);
				}

			}

		}
	}
}
