package cc.funkemunky.daedalus.api.data;

import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.utils.TickTimer;
import cc.funkemunky.daedalus.impl.checks.combat.Killaura;
import cc.funkemunky.daedalus.impl.checks.combat.Reach;
import cc.funkemunky.daedalus.impl.checks.movement.Fly;
import cc.funkemunky.daedalus.impl.checks.movement.Speed;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {
    private UUID uuid;
    private List<Check> checks = Lists.newArrayList();
    private CancelType cancelType = CancelType.NONE;
    public Location setbackLocation, to, from;
    private boolean onGround, fullyInAir, inLiquid, blocksOnTop, pistonsNear, onHalfBlock, onClimbable, onIce, collidesHorizontally,
            inWeb, ableToFly, creativeMode, invulnerable, flying, onSlimeBefore, isRiptiding = false, generalCancel;
    private Vector lastVelocityVector;
    private BoundingBox boundingBox;
    private TickTimer lastMovementCancel = new TickTimer(), lastVelocity = new TickTimer(), lastServerPos = new TickTimer();
    private float walkSpeed, flySpeed;
    private long ping, lastTransaction;
    public int airTicks, groundTicks, iceTicks;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        loadChecks();
    }

    private void registerCheck(Check check) {
        Bukkit.getPluginManager().registerEvents(check, Daedalus.getInstance());
        EventManager.register(check);

        checks.add(check);
    }

    private void loadChecks() {
        registerCheck(new Killaura("Killaura", CancelType.COMBAT, this));
        registerCheck(new Fly("Fly", CancelType.MOTION, this));
        registerCheck(new Speed("Speed", CancelType.MOTION, this));
        registerCheck(new Reach("Reach", CancelType.COMBAT, this));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
