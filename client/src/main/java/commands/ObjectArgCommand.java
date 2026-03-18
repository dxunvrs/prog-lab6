package commands;

import io.InputManager;
import network.Request;
import network.RequestType;
import utility.ProductForm;

public class ObjectArgCommand extends Command {
    @Inject
    private InputManager inputManager;

    public ObjectArgCommand(String name, String description) {
        super(name, description, 0);
    }

    @Override
    public Request execute(String[] tokens) {
        ProductForm productForm = new ProductForm(inputManager);
        Request request = new Request(RequestType.SERVER_COMMAND, getName());
        request.setObjectArg(productForm.getProduct());
        return request;
    }
}
