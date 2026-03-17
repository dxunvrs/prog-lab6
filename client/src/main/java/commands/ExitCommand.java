package commands;

import exceptions.EndOfExecutionException;
import network.Request;

public class ExitCommand extends Command {
    public ExitCommand() {
        super("exit", "exit - выход", 0);
    }

    @Override
    public Request execute(String[] tokens) {
        throw new EndOfExecutionException("Завершение работы...");
    }
}
