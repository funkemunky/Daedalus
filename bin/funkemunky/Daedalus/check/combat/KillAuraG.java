package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class KillAuraG extends Check {
	
	public static HashMap<UUID, Integer> hits;
	private EntityPlayer npc;
	public static HashMap<UUID, EntityPlayer> entities;

	public KillAuraG(Daedalus Daedalus) {
		super("KillauraD", "Killaura (Bot)", Daedalus);
		this.setEnabled(true);
		this.setBannable(true);
		
		this.setMaxViolations(2);
		
		this.hits = new HashMap<UUID, Integer>();
		this.entities = new HashMap<UUID, EntityPlayer>();
	}
	
	private String randomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 16) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	
	@EventHandler
	public void onCombat(PacketUseEntityEvent e) {
		if(!this.hits.containsKey(e.getAttacker().getUniqueId())) {
			this.hits.put(e.getAttacker().getUniqueId(), 0);
			return;
		}
		if(!this.entities.containsKey(e.getAttacker().getUniqueId())) {
			return;
		}
		Player player = e.getAttacker();
	    if(e.getAttacked().getEntityId() == Integer.valueOf(entities.get(player.getUniqueId()).getId())) {
	    	this.hits.put(player.getUniqueId(), this.hits.get(player.getUniqueId()) + 1);
	    }
	    
	    this.entities.get(player.getUniqueId()).setInvisible(false);
	    
	    new BukkitRunnable() {
	    	public void run() {
	    		entities.get(player.getUniqueId()).setInvisible(true);
	    	}
	    }.runTaskLaterAsynchronously(getDaedalus(), 15L);
	    
	    if(hits.get(player.getUniqueId()) >= 3) {
	    	getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[0]);
	    	hits.remove(e.getAttacker());
	    }
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent e) {
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), randomString()), new PlayerInteractManager(nmsWorld));
        entities.put(e.getPlayer().getUniqueId(), npc);
        npc.setInvisible(true);
        PlayerConnection connection = ((CraftPlayer) e.getPlayer()).getHandle().playerConnection;
        Location loc = e.getPlayer().getLocation();
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
        connection.sendPacket(new PacketPlayOutPlayerInfo().addPlayer(npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn((EntityHuman)npc));
	}
	
	
 }
