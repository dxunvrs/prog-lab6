package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class InfoCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public InfoCommand() {
        super("info", "info - информация о коллекции", CommandType.NO_ARGS);
    }

    @Override
    public Response execute(Request request) {
        String responseMessage = collectionManager.getCollectionInfo();
        return new Response(ResponseType.OK, responseMessage);
    }
}
