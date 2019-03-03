package cc.funkemunky.anticheat.api.utils;

import cc.funkemunky.anticheat.Daedalus;


public class TickTimer {
    private int ticks = 0, defaultPassed;

    public TickTimer(int defaultPassed) {
        this.defaultPassed = defaultPassed;
    }

    public void reset() {
        ticks = Daedalus.getInstance().getCurrentTicks();
    }

    public boolean hasPassed() {
        return Daedalus.getInstance().getCurrentTicks() - ticks > defaultPassed;
    }

    public boolean hasPassed(int amount) {
        return Daedalus.getInstance().getCurrentTicks() - ticks > amount;
    }

    public boolean hasNotPassed() {
        return Daedalus.getInstance().getCurrentTicks() - ticks <= defaultPassed;
    }

    public boolean hasNotPassed(int amount) {
        return Daedalus.getInstance().getCurrentTicks() - ticks <= amount;
    }

    public int getPassed() {
        return Daedalus.getInstance().getCurrentTicks() - ticks;
    }
}
