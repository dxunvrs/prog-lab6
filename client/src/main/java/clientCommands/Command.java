package clientCommands;

import network.Request;

public abstract class Command {
    private final String name;
    private final String description;
    private final int expectArgs;

    public Command(String name, String description, int expectArgs) {
        this.name = name;
        this.description = description;
        this.expectArgs = expectArgs;
    }

    public abstract Request execute(String[] tokens);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getExpectArgs() { return expectArgs; }
}
