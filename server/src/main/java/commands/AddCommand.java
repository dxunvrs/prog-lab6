package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class AddCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public AddCommand() {
        super("add", "add - добавление нового элемента", CommandType.OBJECT_ARG);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.addProduct(request.getObjectArg());
        return new Response(ResponseType.OK, "Продукт добавлен");
    }
}
