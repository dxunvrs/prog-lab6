package commands;

import network.Request;
import network.RequestType;

public class IntArgCommand extends Command {
    public IntArgCommand(String name, String description) {
        super(name, description, 1);
    }

    @Override
    public Request execute(String[] tokens) {
        Request request = new Request(RequestType.SERVER_COMMAND, getName());
        request.setIntArg(Integer.parseInt(tokens[1])); // выдает ошибку преобразования -> обработчик в CommandManager
        return request;
    }
}
