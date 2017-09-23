package funkemunky.Daedalus.tasks;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.combat.KillAuraG;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PlayerConnection;

public class HitBoxTask {

	public HitBoxTask(Daedalus Daedalus) {
		new BukkitRunnable() {
			public void run() {
				teleportEntities();
			}
		}.runTaskTimerAsynchronously(Daedalus, 0L, 2L);
	}
	
	public void teleportEntities() {
		for(Player online : Bukkit.getOnlinePlayers()) {
		    EntityPlayer bot = KillAuraG.entities.get(online.getUniqueId());
		    bot.setLocation(online.getLocation().getX() + 1.0D,online.getLocation().getY() + 4.3D,online.getLocation().getZ() + 1.2D, online.getLocation().getPitch(), online.getLocation().getYaw());
		    PlayerConnection connection = ((CraftPlayer)online).getHandle().playerConnection;
		    connection.sendPacket((Packet)new PacketPlayOutEntityTeleport(bot));
            connection.sendPacket((Packet)new PacketPlayOutEntityHeadRotation((Entity)bot, this.getCompressedAngle(online.getLocation().getYaw())));

		    bot.teleportTo(online.getLocation().clone().add(1.0D, 4.3D, 1.2D), true);
		}
	}
	
    private byte getCompressedAngle(float value) {
        return (byte)(value * 256.0f / 360.0f);
    }

}
