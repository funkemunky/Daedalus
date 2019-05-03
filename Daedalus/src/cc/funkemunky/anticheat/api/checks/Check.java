package cc.funkemunky.anticheat.api.checks;

import cc.funkemunky.anticheat.Daedalus;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.system.Listener;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.JsonMessage;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@Setter
public abstract class Check implements Listener, org.bukkit.event.Listener {
    private String name;
    private CancelType cancelType;
    private PlayerData data;
    private int maxVL;
    private boolean enabled, executable, cancellable, developer;
    private Verbose lagVerbose = new Verbose();
    private long lastAlert;
    private List<String> execCommand = new ArrayList<>();
    private Map<String, Object> settings = new HashMap<>();
    private String alertMessage = "";
    private int vl;

    public Check(String name, CancelType cancelType, int maxVL) {
        this.name = name;
        this.cancelType = cancelType;
        this.maxVL = maxVL;

        enabled = cancellable = executable = true;

        developer = false;

        alertMessage = CheckSettings.alertMessage.replaceAll("%check%", name);

        loadFromConfig();
    }

    public Check(String name, CancelType cancelType, PlayerData data, int maxVL) {
        this.name = name;
        this.cancelType = cancelType;
        this.data = data;
        this.maxVL = maxVL;

        enabled = executable = cancellable = true;

        developer = false;

        alertMessage = CheckSettings.alertMessage.replaceAll("%check%", name);
        loadFromConfig();
    }

    public Check(String name, CancelType cancelType, int maxVL, boolean enabled, boolean executable, boolean cancellable) {
        this.name = name;
        this.cancelType = cancelType;
        this.maxVL = maxVL;
        this.enabled = enabled;
        this.executable = executable;
        this.cancellable = cancellable;

        developer = false;

        alertMessage = CheckSettings.alertMessage.replaceAll("%check%", name);
        loadFromConfig();
    }

    protected void flag(String information, boolean cancel, boolean ban) {
        Atlas.getInstance().getThreadPool().execute(() -> {
            if (data.getLastLag().hasPassed() || lagVerbose.flag(4, 500L)) {
                vl++;
                if (vl > maxVL && executable && ban && !Daedalus.getInstance().getCheckManager().getBannedPlayers().contains(data.getUuid())) {
                    Daedalus.getInstance().getCheckManager().getBannedPlayers().add(data.getUuid());
                    new BukkitRunnable() {
                        public void run() {
                            execCommand.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%player%", getData().getPlayer().getName()).replaceAll("%check%", getName())));
                        }
                    }.runTaskLater(Daedalus.getInstance(), 30);
                }

                data.getLastFlag().reset();

                if (cancel && cancellable) data.setCancelType(cancelType);

                if (System.currentTimeMillis() - lastAlert > CheckSettings.alertsDelay) {
                    JsonMessage message = new JsonMessage();

                    message.addText(Color.translate(alertMessage.replaceAll("%player%", data.getPlayer().getName()).replaceAll("%vl%", String.valueOf(vl)).replaceAll("%info%", information))).addHoverText(Color.Gray + information);
                    Daedalus.getInstance().getDataManager().getDataObjects().values().stream().filter(PlayerData::isAlertsEnabled).forEach(data -> message.sendToPlayer(data.getPlayer()));
                    lastAlert = System.currentTimeMillis();
                }

                if(CheckSettings.testMode && !data.isAlertsEnabled()) {
                    JsonMessage message = new JsonMessage();

                    message.addText(Color.translate(alertMessage.replaceAll("%player%", data.getPlayer().getName()).replaceAll("%vl%", String.valueOf(vl)).replaceAll("%info%", information))).addHoverText(Color.Gray + information);
                    message.sendToPlayer(data.getPlayer());
                }

                if(CheckSettings.printToConsole) {
                    MiscUtils.printToConsole(alertMessage.replaceAll("%player%", data.getPlayer().getName()).replaceAll("%vl%", String.valueOf(vl)));
                }
            }
        });
    }

    private void loadFromConfig() {
        if (Daedalus.getInstance().getConfig().get("checks." + name) != null) {
            maxVL = Daedalus.getInstance().getConfig().getInt("checks." + name + ".maxVL");
            enabled = Daedalus.getInstance().getConfig().getBoolean("checks." + name + ".enabled");
            executable = Daedalus.getInstance().getConfig().getBoolean("checks." + name + ".executable");
            cancellable = Daedalus.getInstance().getConfig().getBoolean("checks." + name + ".cancellable");
            Daedalus.getInstance().getConfig().getStringList("checks." + name + ".execCommands").forEach(cmd -> {
                if(cmd.equals("%global%")) {
                    execCommand.addAll(CheckSettings.executableCommand);
                } else {
                    execCommand.add(cmd);
                }
            });
        } else {
            Daedalus.getInstance().getConfig().set("checks." + name + ".maxVL", maxVL);
            Daedalus.getInstance().getConfig().set("checks." + name + ".enabled", enabled);
            Daedalus.getInstance().getConfig().set("checks." + name + ".executable", executable);
            Daedalus.getInstance().getConfig().set("checks." + name + ".cancellable", cancellable);
            Daedalus.getInstance().getConfig().set("checks." + name + ".execCommands", Collections.singletonList("%global%"));

            Daedalus.getInstance().saveConfig();
        }
    }

    public void debug(String debugString) {
        Daedalus.getInstance().getDataManager().getDataObjects().values().stream()
                .filter(dData -> dData.getDebuggingPlayer() != null && dData.getDebuggingCheck() != null && dData.getDebuggingCheck().getName().equals(name) && dData.getDebuggingPlayer().equals(data.getUuid()))
                .forEach(dData -> dData.getPlayer().sendMessage(Color.translate("&8[&cDebug&8] &7" + debugString)));
    }

    public abstract void onPacket(Object packet, String packetType, long timeStamp);

    public abstract void onBukkitEvent(Event event);
}
