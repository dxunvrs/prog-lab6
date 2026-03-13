package clientCommands;

import network.Request;
import network.RequestType;

public class NoArgsCommand extends Command {
    public NoArgsCommand(String name, String description) {
        super(name, description, 0);
    }

    @Override
    public Request execute(String[] tokens) {
        return new Request(RequestType.SERVER_COMMAND, getName());
    }
}
