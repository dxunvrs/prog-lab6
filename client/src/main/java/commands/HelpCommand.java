package commands;

import core.CommandManager;
import network.Request;

public class HelpCommand extends Command {
    @Inject
    private CommandManager commandManager;

    public HelpCommand() {
        super("help", "help - список доступных команд", 0);
    }

    @Override
    public Request execute(String[] tokens) {
        System.out.println(commandManager.getFormattedCommandsList());
        return null;
    }
}
