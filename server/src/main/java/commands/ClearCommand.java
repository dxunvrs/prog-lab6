package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class ClearCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public ClearCommand() {
        super("clear", "clear - очистить коллекцию", CommandType.NO_ARGS);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.clearCollection();
        return new Response(ResponseType.OK, "Коллекция очищена");
    }
}
