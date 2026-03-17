package commands;

import core.CollectionManager;
import network.Request;
import network.Response;
import network.ResponseType;

public class SumOfPriceCommand extends Command {
    @Inject
    private CollectionManager collectionManager;

    public SumOfPriceCommand() {
        super("sum_of_price", "sum_of_price - вывести сумму цен всех элементов коллекции", CommandType.NO_ARGS);
    }

    @Override
    public Response execute(Request request) {
        String responseMessage = "Сумма цен всех элементов коллекции: " + collectionManager.getSumOfPrice();
        return new Response(ResponseType.OK, responseMessage);
    }
}
