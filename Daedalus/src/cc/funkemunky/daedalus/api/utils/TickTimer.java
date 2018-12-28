package cc.funkemunky.daedalus.api.utils;

import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.data.PlayerData;


public class TickTimer {
    private int ticks = 0;

    public void reset() {
        ticks = Daedalus.getInstance().getCurrentTicks();
    }

    public boolean hasPassed(int amount) {
        return Daedalus.getInstance().getCurrentTicks() - ticks > amount;
    }

    public boolean hasNotPassed(int amount) {
        return Daedalus.getInstance().getCurrentTicks() - ticks <= amount;
    }

    public int getPassed() {
        return Daedalus.getInstance().getCurrentTicks() - ticks;
    }
}
