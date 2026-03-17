package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class SortCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public SortCommand() {
        super("sort", "sort - сортировка коллекции в естественном порядке (по id)", CommandType.NO_ARGS);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.sort();
        String responseMessage = "Коллекция отсортирована в естественном порядке, введите show для просмотра";
        return new Response(ResponseType.OK, responseMessage);
    }
}
