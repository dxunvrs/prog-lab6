package commands;

import network.Request;
import network.Response;

public abstract class Command {
    private final String name;
    private final String description;
    private final CommandType type;

    public Command(String name, String description, CommandType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public abstract Response execute(Request request);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandType getType() {
        return type;
    }
}
