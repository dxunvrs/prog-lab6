package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class UpdateCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public UpdateCommand() {
        super("update", "update id - обновить значение элемента по заданному id", CommandType.MIXED_ARGS);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.updateProductById(request.getIntArg(), request.getObjectArg());
        String responseMessage = "Продукт с id=" + request.getIntArg() + " обновлен";
        return new Response(ResponseType.OK, responseMessage);
    }
}
