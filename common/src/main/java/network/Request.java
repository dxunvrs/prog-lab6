package network;

import com.fasterxml.jackson.annotation.JsonInclude;
import models.Product;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
    private String commandName;
    private RequestType type;

    private String stringArg;
    private Product objectArg;
    private int intArg;

    public Request() {}

    public Request(RequestType type, String commandName) {
        this.type = type;
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public RequestType getType() {
        return type;
    }

    public String getStringArg() {
        return stringArg;
    }

    public Product getObjectArg() {
        return objectArg;
    }

    public int getIntArg() {
        return intArg;
    }

    public void setStringArg(String value) {
        stringArg = value;
    }

    public void setObjectArg(Product value) {
        objectArg = value;
    }

    public void setIntArg(int value) {
        intArg = value;
    }
}
