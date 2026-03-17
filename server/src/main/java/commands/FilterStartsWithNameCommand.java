package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class FilterStartsWithNameCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public FilterStartsWithNameCommand() {
        super("filter_starts_with_name", "filter_starts_with_name - вывести элементы, название которых начинается с заданной подстроки", CommandType.STRING_ARG);
    }

    @Override
    public Response execute(Request request) {
        String responseMessage = collectionManager.getFormattedCollection(product -> product.getName().startsWith(request.getStringArg()));
        return new Response(ResponseType.OK, responseMessage);
    }
}
