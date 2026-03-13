package clientCommands;

import network.Request;
import network.RequestType;

public class StringArgCommand extends Command {
    public StringArgCommand(String name, String description) {
        super(name, description, 1);
    }

    @Override
    public Request execute(String[] tokens) {
        Request request = new Request(RequestType.SERVER_COMMAND, getName());
        request.setStringArg(tokens[1]);
        return request;
    }
}
