package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class RemoveCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public RemoveCommand() {
        super("remove_by_id", "remove_by_id - удалить элемент из коллекции по id", CommandType.INT_ARG);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.removeProductById(request.getIntArg());
        String responseMessage = "Продукт с id=" + request.getIntArg() + " удален";
        return new Response(ResponseType.OK, responseMessage);
    }
}
