package funkemunky.Daedalus.check.combat;

import org.bukkit.event.EventHandler;
import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketPlayerEvent;
import funkemunky.Daedalus.packets.events.PacketPlayerType;
import funkemunky.Daedalus.utils.Chance;

public class Twitch
        extends Check
{
    public Twitch(Daedalus Daedalus)
    {
        super("Twitch", "Twitch", Daedalus);

        this.setBannable(true);
    }

    @EventHandler
    public void Player(PacketPlayerEvent e)
    {
        if (e.getType() != PacketPlayerType.LOOK) {
            return;
        }
        if ((e.getPitch() > 90.1F) || (e.getPitch() < -90.1F)) {
            getDaedalus().logCheat(this, e.getPlayer(), null, Chance.HIGH, new String[0]);
        }
    }
}
