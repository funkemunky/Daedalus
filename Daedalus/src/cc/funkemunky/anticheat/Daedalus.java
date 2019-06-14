package cc.funkemunky.anticheat;

import cc.funkemunky.anticheat.api.checks.CheckManager;
import cc.funkemunky.anticheat.api.data.DataManager;
import cc.funkemunky.anticheat.api.event.TickEvent;
import cc.funkemunky.anticheat.api.utils.Message;
import cc.funkemunky.anticheat.api.utils.updater.Updater;
import cc.funkemunky.anticheat.impl.commands.daedalus.DaedalusCommand;
import cc.funkemunky.anticheat.impl.listeners.FunkeListeners;
import cc.funkemunky.anticheat.impl.listeners.PacketListeners;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.utils.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

@Getter
public class Daedalus extends JavaPlugin {
    @Getter
    private static Daedalus instance;
    private DataManager dataManager;
    private CheckManager checkManager;
    private int currentTicks;
    private long lastTick, tickElapsed, profileStart;
    private ScheduledExecutorService executorService;
    private BaseProfiler profiler;
    private File messagesFile;
    private FileConfiguration messages;

    private String[] requiredVersionsOfAtlas = new String[] {"1.2.1", "1.2", "1.2.2", "1.3-PRE-b6"};

    @Override
    public void onEnable() {
        //This allows us to access this class's contents from others places.
        instance = this;

        saveDefaultConfig();
        createMessages();

        if(Bukkit.getPluginManager().isPluginEnabled("Atlas") && Arrays.stream(requiredVersionsOfAtlas).anyMatch(version -> Bukkit.getPluginManager().getPlugin("Atlas").getDescription().getVersion().equals(version))) {

            profiler = new BaseProfiler();
            profileStart = System.currentTimeMillis();

            //Starting up our utilities, managers, and tasks.
            checkManager = new CheckManager();
            dataManager = new DataManager();

            startScanner();

            if (!checkUpdater()) {
                MiscUtils.printToConsole("&c&lInvalid version of Daedalus, please update at https://www.spigotmc.org/resources/daedalus-anticheat-advanced-cheat-detection-1-7-1-13.53721/");
                Bukkit.getServer().shutdown();
                return;
            } else {
                MiscUtils.printToConsole("&aValid license, enjoy Daedalus!");
            }

            runTasks();
            registerCommands();
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "You do not the required Atlas dependency installed! You must download Atlas v" + requiredVersionsOfAtlas[0] + " for Daedalus to work properly.");
            if(getConfig().getBoolean("dependencies.Atlas.autoDownload")) {
                Bukkit.getLogger().log(Level.INFO, "Downloading the appropriate version of Atlas now...");
                Updater.downloadAppropriateVersion();
                Bukkit.getLogger().log(Level.INFO, "Download complete! Please restart your server.");
            } else {
                Bukkit.getLogger().log(Level.INFO, "You can turn set the dependencies.Atlas.autoDownload setting to true in the config to auto-magically download the proper version.");
            }
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        //Registering all the commands
    }

    public void onDisable() {
        Atlas.getInstance().getEventManager().unregisterAll(this);
        org.bukkit.event.HandlerList.unregisterAll(this);
        dataManager.getDataObjects().clear();
        checkManager.getChecks().clear();
        Bukkit.getScheduler().cancelTasks(this);
        executorService.shutdownNow();
    }

    private void runTasks() {
        //This allows us to use ticks for time comparisons to allow for more parrallel calculations to actual Minecraft
        //and it also has the added benefit of being lighter than using System.currentTimeMillis.
        new BukkitRunnable() {
            public void run() {
                TickEvent tickEvent = new TickEvent(currentTicks++);

                Atlas.getInstance().getEventManager().callEvent(tickEvent);
            }
        }.runTaskTimerAsynchronously(this, 1L, 1L);

        new BukkitRunnable() {
            public void run() {
                long timeStamp = System.currentTimeMillis();
                tickElapsed = timeStamp - lastTick;
                //Bukkit.broadcastMessage(tickElapsed + "ms" + ", " + getTPS());
                lastTick = timeStamp;
            }
        }.runTaskTimer(Daedalus.getInstance(), 0L, 1L);
    }

    public void startScanner() {
        initializeScanner(getClass(), this);
    }

    private void registerCommands() {
        Atlas.getInstance().getFunkeCommandManager().addCommand(this, new DaedalusCommand());
    }

    public double getTPS() {
        return Bukkit.getServer().getVersion().toLowerCase().contains("paper") ? ReflectionsUtil.getTPS(Bukkit.getServer()) : 1000D / tickElapsed;
    }

    public double getTPS(RoundingMode mode, int places) {
        return MathUtils.round(getTPS(), places, mode);
    }

    private boolean checkUpdater() {
        boolean enabled = true;
        try {
            URL url = new URL("https://pastebin.com/raw/ZJdP0Shd");
            Scanner scanner = new Scanner(url.openStream());
            enabled = Boolean.valueOf(scanner.nextLine());
            scanner.close();
        } catch (Exception ex) {
            MiscUtils.printToConsole("&aValid license, enjoy Daedalus!");
        }
        return enabled;

    }

    public void reloadDaedalus() {
        Atlas.getInstance().getThreadPool().execute(() -> {
            cc.funkemunky.anticheat.api.utils.MiscUtils.unloadPlugin("Daedalus");
            cc.funkemunky.anticheat.api.utils.MiscUtils.loadPlugin("Daedalus");
        });
    }

    public FileConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }


    public void saveMessages() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            getMessages().save(messagesFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + messagesFile, ex);
        }
    }

    public void createMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        // Look for defaults in the jar
        try {
            Reader defConfigStream = new InputStreamReader(this.getResource("messages.yml"), "UTF8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            messages.setDefaults(defConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeScanner(Class<?> mainClass, Plugin plugin) {
        ClassScanner.scanFile(null, mainClass).stream().filter(c -> {
            try {
                Class clazz = Class.forName(c);

                return clazz.isAnnotationPresent(Init.class);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return false;
        }).sorted(Comparator.comparingInt(c -> {
            try {
                Class clazz = Class.forName(c);

                Init annotation = (Init) clazz.getAnnotation(Init.class);

                return annotation.priority().getPriority();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return 3;
        })).forEachOrdered(c -> {
            try {
                Class clazz = Class.forName(c);

                if(clazz.isAnnotationPresent(Init.class)) {
                    Object obj = clazz.getSimpleName().equals(mainClass.getSimpleName()) ? plugin : clazz.newInstance();
                    Init init = (Init) clazz.getAnnotation(Init.class);
                    if (obj instanceof Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Bukkit listener. Registering...");
                        plugin.getServer().getPluginManager().registerEvents((Listener) obj, plugin);
                    } else if (obj instanceof cc.funkemunky.api.event.system.Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + "(deprecated) Atlas listener. Registering...");
                        cc.funkemunky.api.event.system.EventManager.register(plugin, (cc.funkemunky.api.event.system.Listener) obj);
                    } else if (obj instanceof AtlasListener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + "Atlas listener. Registering...");
                        Atlas.getInstance().getEventManager().registerListeners((AtlasListener) obj, plugin);
                    }

                    if(init.commands()) {
                        Atlas.getInstance().getCommandManager().registerCommands(this, obj);
                    }

                    for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        if(field.isAnnotationPresent(ConfigSetting.class)) {
                            String name = field.getAnnotation(ConfigSetting.class).name();
                            String path = field.getAnnotation(ConfigSetting.class).path() + "." + (name.length() > 0 ? name : field.getName());
                            try {
                                MiscUtils.printToConsole("&eFound " + field.getName() + " ConfigSetting (default=" + field.get(obj) + ").");
                                if(plugin.getConfig().get(path) == null) {
                                    MiscUtils.printToConsole("&eValue not found in configuration! Setting default into config...");
                                    plugin.getConfig().set(path, field.get(obj));
                                    plugin.saveConfig();
                                } else {
                                    field.set(obj, plugin.getConfig().get(path));

                                    MiscUtils.printToConsole("&eValue found in configuration! Set value to &a" + plugin.getConfig().get(path));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if(field.isAnnotationPresent(Message.class)) {
                            String name = field.getAnnotation(Message.class).name();
                            String path = field.getAnnotation(Message.class).path() + "." + (name.length() > 0 ? name : field.getName());
                            try {
                                field.setAccessible(true);
                                MiscUtils.printToConsole("&eFound " + field.getName() + " message (default=" + field.get(obj) + ").");
                                if(getMessages().get(path) == null) {
                                    MiscUtils.printToConsole("&eValue not found in messages.yml! Setting default into the config...");
                                    getMessages().set(path, field.get(obj));
                                    saveMessages();
                                } else {
                                    field.set(obj, getMessages().get(path));

                                    MiscUtils.printToConsole("&eValue found in message.yml! Set value to &a" + getMessages().get(path));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
