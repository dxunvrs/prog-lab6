package commands;

import core.CommandManager;
import network.Request;

public class HistoryCommand extends Command {
    @Inject
    private CommandManager commandManager;

    public HistoryCommand() {
        super("history", "history - последние 15 команд", 0);
    }

    @Override
    public Request execute(String[] tokens) {
        System.out.println(commandManager.getFormattedHistory());
        return null;
    }
}
