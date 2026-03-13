package clientCommands;

import core.CommandManager;
import network.Request;
import network.RequestType;

public class HelpCommand extends Command {
    @Inject
    private CommandManager commandManager;

    public HelpCommand() {
        super("help", "help - список доступных команд", 0);
    }

    @Override
    public Request execute(String[] tokens) {
        return new Request(RequestType.CLIENT_COMMAND, commandManager.getFormattedCommandsList());
    }
}
