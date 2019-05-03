package cc.funkemunky.anticheat.api.checks;

import cc.funkemunky.anticheat.Daedalus;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.anticheat.impl.checks.combat.autoclicker.AutoclickerB;
import cc.funkemunky.anticheat.impl.checks.combat.autoclicker.AutoclickerA;
import cc.funkemunky.anticheat.impl.checks.combat.criticals.Criticals;
import cc.funkemunky.anticheat.impl.checks.combat.fastbow.Fastbow;
import cc.funkemunky.anticheat.impl.checks.combat.killaura.*;
import cc.funkemunky.anticheat.impl.checks.combat.reach.Reach;
import cc.funkemunky.anticheat.impl.checks.movement.*;
import cc.funkemunky.anticheat.impl.checks.player.*;
import cc.funkemunky.anticheat.impl.checks.player.Timer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CheckManager {
    private List<Check> checks = new ArrayList<>();
    private List<UUID> bannedPlayers = new ArrayList<>();

    public CheckManager() {
        checks = loadChecks();
    }

    public List<Check> loadChecks() {
        List<Check> checks = new ArrayList<>();
        checks.add(new AutoclickerA("Autoclicker (Type A)", CancelType.COMBAT, 20));
        checks.add(new AutoclickerB("Autoclicker (Type B)", CancelType.COMBAT, 20));
        checks.add(new KillauraA("Killaura (Type A)", CancelType.COMBAT, 150));
        checks.add(new KillauraB("Killaura (Type B)", CancelType.COMBAT, 50));
        checks.add(new KillauraC("Killaura (Type C)", CancelType.COMBAT, 80));
        checks.add(new FlyA("Fly (Type A)", CancelType.MOTION, 125));
        checks.add(new FlyB("Fly (Type B)", CancelType.MOTION, 100, true, false, true));
        checks.add(new SpeedA("Speed (Type A)", CancelType.MOTION, 100));
        checks.add(new SpeedB("Speed (Type B)", CancelType.MOTION, 125));
        checks.add(new Timer("Timer", CancelType.MOTION, 100));
        checks.add(new GroundSpoof("GroundSpoof", CancelType.MOTION, 100));
        checks.add(new Reach("Reach", CancelType.COMBAT, 50));
        checks.add(new Regen("Regen", CancelType.HEALTH, 20));
        checks.add(new Fastbow("Fastbow", CancelType.PROJECTILE, 40));
        checks.add(new BadPacketsA("BadPackets (Type A)", CancelType.MOTION, 40));
        checks.add(new BadPacketsB("BadPackets (Type B)", CancelType.MOTION, 40, true, false, true));
        checks.add(new BadPacketsC("BadPackets (Type C)", CancelType.MOTION, 40));
        checks.add(new FastLadder("FastLadder", CancelType.MOTION, 50));
        checks.add(new Criticals("Criticals", CancelType.COMBAT, 40));

        for (Check check : checks) {
            Arrays.stream(check.getClass().getDeclaredFields()).filter(field -> {
                field.setAccessible(true);

                return field.isAnnotationPresent(Setting.class);
            }).forEach(field -> {
                try {
                    field.setAccessible(true);

                    String path = "checks." + check.getName() + ".settings." + field.getName();
                    if (Daedalus.getInstance().getConfig().get(path) != null) {
                        Object val = Daedalus.getInstance().getConfig().get(path);

                        if (val instanceof Double && field.get(check) instanceof Float) {
                            field.set(check, (float) (double) val);
                        } else {
                            field.set(check, val);
                        }
                    } else {
                        Daedalus.getInstance().getConfig().set("checks." + check.getName() + ".settings." + field.getName(), field.get(check));
                        Daedalus.getInstance().saveConfig();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
        return checks;
    }

    public void registerCheck(Check check) {
        checks.add(check);
    }

    public boolean isCheck(String name) {
        return checks.stream().anyMatch(check -> check.getName().equalsIgnoreCase(name));
    }

    public void loadChecksIntoData(PlayerData data) {
        List<Check> checks = loadChecks();

        data.getChecks().clear();

        checks.forEach(check -> check.setData(data));

        data.setChecks(checks);
    }

    public Check getCheck(String name) {
        return checks.stream().filter(check -> check.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void removeCheck(String name) {
        Optional<Check> opCheck = checks.stream().filter(check -> check.getName().equalsIgnoreCase(name)).findFirst();

        opCheck.ifPresent(check -> checks.remove(check));
    }
}
