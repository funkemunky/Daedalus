package cc.funkemunky.anticheat.impl.commands.daedalus;

import cc.funkemunky.anticheat.Daedalus;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.anticheat.impl.commands.daedalus.arguments.*;
import cc.funkemunky.api.commands.CommandMessages;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;

public class DaedalusCommand extends FunkeCommand {
    public DaedalusCommand() {
        super(Daedalus.getInstance(), "daedalus", "Daedalus", "The Daedalus anticheat main command.", "daedalus.command");
        setAdminPerm("daedalus.*");
    }

    @Override
    protected void addArguments() {
        setCommandMessages(new CommandMessages(
                Color.translate(Messages.noPermission),
                Color.translate(Messages.invalidArguments),
                Color.translate(Messages.playerOnly),
                Color.translate(Messages.consoleOnly),
                Color.translate(Messages.primaryColor),
                Color.translate(Messages.secondaryColor),
                Color.translate(Messages.titleColor),
                Color.translate(Messages.errorColor),
                Color.translate(Messages.valueColor),
                Color.translate(Messages.successColor)));
        getArguments().add(new ReloadArgument(this, "reload", "reload", "reload the Daedalus config.", "daedalus.reload"));
        getArguments().add(new LagArgument(this, "lag", "lag <profile,server,player> [args]", "view extensive lag information.", "daedalus.lag"));
        getArguments().add(new AlertsArgument(this, "alerts", "alerts", "toggle your alerts", "daedalus.alerts"));
        getArguments().add(new DebugArgument(this, "debug", "debug <check,none> [player]", "debug a check.", "daedalus.debug"));
        getArguments().add(new BoxWandArgument(this, "boxwand", "boxwand", "receive the magic box wand.", "daedalus.boxwand"));
        getArguments().add(new BroadcastArg(this, "broadcast", "broadcast <msg>", "broadcast a message from console only."));
    }
}
