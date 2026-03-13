package clientCommands;

import core.CommandManager;
import network.Request;
import network.RequestType;

public class HistoryCommand extends Command {
    @Inject
    private CommandManager commandManager;

    public HistoryCommand() {
        super("history", "history - последние 15 команд", 0);
    }

    @Override
    public Request execute(String[] tokens) {
        return new Request(RequestType.CLIENT_COMMAND, commandManager.getFormattedHistory());
    }
}
