package anticheat.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Ping {

    public static Object getNmsPlayer(Player p) throws Exception{
        Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }
    
    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static int getPing(Player who) {
        try {
            String bukkitversion = Bukkit.getServer().getClass().getPackage()
                    .getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit."
                    + bukkitversion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(who);
            Integer ping = (Integer) handle.getClass().getDeclaredField("ping")
                    .get(handle);

            return ping.intValue();
        } catch (Exception e) {
            return -1;
        }
    }

}
