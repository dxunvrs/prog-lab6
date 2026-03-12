package network;

public class Request {
    private String commandName;

    public Request() {}

    public Request(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
