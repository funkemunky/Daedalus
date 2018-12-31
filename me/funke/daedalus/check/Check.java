package me.funke.daedalus.check;

import me.funke.daedalus.Daedalus;
import me.funke.daedalus.utils.TxtFile;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check implements Listener {
    public Map<String, List<String>> DumpLogs = new HashMap<>();
    private String Identifier;
    private String Name;
    private me.funke.daedalus.Daedalus Daedalus;
    private boolean Enabled = true;
    private boolean BanTimer = false;
    private boolean Bannable = true;
    private boolean JudgementDay = false;
    private Integer MaxViolations = 5;
    private Integer ViolationsToNotify = 1;
    private Long ViolationResetTime = 600000L;

    public Check(String Identifier, String Name, Daedalus Daedalus) {
        this.Name = Name;
        this.Daedalus = Daedalus;
        this.Identifier = Identifier;
    }

    public void dumplog(Player player, String log) {
        if (!this.DumpLogs.containsKey(player.getName())) {
            List<String> logs = new ArrayList<>();
            logs.add(log);
            this.DumpLogs.put(player.getName(), logs);
        } else {
            this.DumpLogs.get(player.getName()).add(log);
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean isEnabled() {
        return this.Enabled;
    }

    public void setEnabled(boolean Enabled) {
        if (Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled") != Enabled
                && Daedalus.getConfig().get("checks." + this.getIdentifier() + ".enabled") != null) {
            this.Enabled = Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled");
            return;
        }
        if (Enabled) {
            if (!isEnabled()) {
                this.Daedalus.RegisterListener(this);
            }
        } else if (isEnabled()) {
            HandlerList.unregisterAll(this);
        }
        this.Enabled = Enabled;
    }

    public boolean isBannable() {
        return this.Bannable;
    }

    public void setBannable(boolean Bannable) {
        if (Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable") != Bannable
                && Daedalus.getConfig().get("checks." + this.getIdentifier() + ".bannable") != null) {
            this.Bannable = Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable");
            return;
        }
        this.Bannable = Bannable;
    }

    public boolean hasBanTimer() {
        return this.BanTimer;
    }

    public boolean isJudgmentDay() {
        return this.JudgementDay;
    }

    public Daedalus getDaedalus() {
        return this.Daedalus;
    }

    public boolean hasDump(Player player) {
        return DumpLogs.containsKey(player.getName());
    }

    public void clearDump(Player player) {
        DumpLogs.remove(player.getName());
    }

    public void clearDumps() {
        DumpLogs.clear();
    }

    public Integer getMaxViolations() {
        return this.MaxViolations;
    }

    public void setMaxViolations(int MaxViolations) {
        if (Daedalus.getConfig().getInt("checks." + this.getIdentifier() + ".maxViolations") != MaxViolations
                && Daedalus.getConfig().get("checks." + this.getIdentifier() + ".maxViolations") != null) {
            this.MaxViolations = Daedalus.getConfig().getInt("checks." + this.getIdentifier() + ".maxViolations");
            return;
        }
        this.MaxViolations = MaxViolations;
    }

    public Integer getViolationsToNotify() {
        return this.ViolationsToNotify;
    }

    public void setViolationsToNotify(int ViolationsToNotify) {
        this.ViolationsToNotify = ViolationsToNotify;
    }

    public Long getViolationResetTime() {
        return this.ViolationResetTime;
    }

    public void setViolationResetTime(long ViolationResetTime) {
        this.ViolationResetTime = ViolationResetTime;
    }

    public void checkValues() {
        if (Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled")) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
        if (Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable")) {
            this.setBannable(true);
        } else {
            this.setEnabled(false);
        }
    }

    public void setAutobanTimer(boolean BanTimer) {
        if ((Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".banTimer") != BanTimer
                && Daedalus.getConfig().get("checks." + this.getIdentifier() + ".banTimer") != null)) {
            this.BanTimer = Daedalus.getConfig().getBoolean("checks." + this.getIdentifier() + ".banTimer");
            return;
        }
        this.BanTimer = BanTimer;
    }

    public void setJudgementDay(boolean JudgementDay) {
        this.JudgementDay = JudgementDay;
    }

    public String getName() {
        return this.Name;
    }

    public String getIdentifier() {
        return this.Identifier;
    }

    public List<String> getDump(Player player) {
        return this.DumpLogs.get(player.getName());
    }

    public String dump(String player) {
        if (!this.DumpLogs.containsKey(player)) {
            return null;
        }
        TxtFile file = new TxtFile(this.getDaedalus(), "/Dumps", player + "_" + this.getIdentifier());
        file.clear();
        for (String Line : this.DumpLogs.get(player)) {
            file.addLine(Line);
        }
        file.write();
        return file.getName();
    }
}