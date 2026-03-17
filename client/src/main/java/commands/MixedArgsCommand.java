package commands;

import core.InputReader;
import network.Request;
import network.RequestType;
import utility.ProductForm;

public class MixedArgsCommand extends Command {
    @Inject
    private InputReader inputReader;

    public MixedArgsCommand(String name, String description) {
        super(name, description, 1);
    }

    @Override
    public Request execute(String[] tokens) {
        Request request = new Request(RequestType.SERVER_COMMAND, getName());
        request.setIntArg(Integer.parseInt(tokens[1]));
        ProductForm productForm = new ProductForm(inputReader);
        request.setObjectArg(productForm.getProduct());
        return request;
    }
}
