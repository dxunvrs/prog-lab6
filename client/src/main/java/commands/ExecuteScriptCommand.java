package commands;

import io.InputManager;
import exceptions.ScriptExecutionException;
import network.Request;

import java.io.IOException;

public class ExecuteScriptCommand extends Command {
    @Inject
    private InputManager inputManager;

    public ExecuteScriptCommand() {
        super("execute_script", "execute_script - выполнение скрипта из файла", 1);
    }

    @Override
    public Request execute(String[] tokens) {
        try {
            inputManager.enqueueScript(tokens[1]);
            System.out.println("Начало выполнения скрипта: " + tokens[1]);
        } catch (IOException e) {
            throw new ScriptExecutionException("Ошибка чтения " + e.getMessage());
        }
        return null;
    }
}
