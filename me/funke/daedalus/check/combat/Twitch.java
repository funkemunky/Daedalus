package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketPlayerEvent;
import me.funke.daedalus.packets.events.PacketPlayerType;
import me.funke.daedalus.utils.Chance;
import org.bukkit.event.EventHandler;

public class Twitch extends Check {
    public Twitch(me.funke.daedalus.Daedalus Daedalus) {
        super("Twitch", "Twitch", Daedalus);

        this.setEnabled(true);
        this.setBannable(true);

        setMaxViolations(5);
    }

    @EventHandler
    public void Player(PacketPlayerEvent e) {
        if (e.getType() != PacketPlayerType.LOOK) return;
        if ((e.getPitch() > 90.1F) || (e.getPitch() < -90.1F)) {
            getDaedalus().logCheat(this, e.getPlayer(), null, Chance.HIGH);
        }
    }
}
