package core;

import commands.Command;
import commands.Inject;
import exceptions.EndOfExecutionException;
import network.Request;
import network.Response;
import utility.ExecutionResponse;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CommandManager {
    //private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private final Map<String, Command> commands = new HashMap<>();

    private final List<String> commandsHistory = new LinkedList<>();

    //private final CollectionManager collectionManager;
    private final InputReader reader;
    private ConnectionManager connectionManager;
    //private final FileManager fileManager;

    public CommandManager(InputReader reader) {
        this.reader = reader;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public boolean executeCommand(String line) {
        String[] tokens = line.split(" ");
        Command command = commands.get(tokens[0]);
        if (command == null) {
            // logger.warn("Пользователь ввел некорректную команду {}", tokens[0]);
            System.out.println("Команда " + tokens[0] + " не найдена");
            return true;
        }
        if (command.getExpectArgs() != tokens.length-1) {
            // logger.warn("Пользователь ввел неверное количество аргументов {} для команды {}", tokens.length-1, command.getName());
            System.out.println("Ожидалось " + command.getExpectArgs() + " аргументов, получено " + (tokens.length-1));
            return true;
        }
        try {
            // logger.debug("Начало выполнения команды {}", command.getName());

            ExecutionResponse executionResponse = command.execute(tokens);
            System.out.println(executionResponse.message());

            Request request = new Request(command.getName());
            Response response = connectionManager.sendAndReceive(request);
            System.out.println("Ответ от сервера: " + response.getMessage() + ", успех: " + response.isSuccess());

            // logger.info("Сообщение команды: {}", executionResponse.message());
            addCommandToHistory(command.getName());
            // logger.debug("Команда {} добавлена в историю", command.getName());

            return !executionResponse.shouldExit();
//        } catch (InvalidIdException | IdNotFoundException | SaveException | ScriptExecutionException e) {
//            return notifyError(e);
        } catch (EndOfExecutionException e) {
            // logger.info("Завершение программы", e);
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean notifyError(Exception e) {
        // logger.error(e.getMessage(), e);
        System.out.println(e.getMessage());
        return true;
    }

    private void addCommandToHistory(String commandName) {
        commandsHistory.add(commandName);
        if (commandsHistory.size() > 15) {
            commandsHistory.remove(0);
        }
    }

    public String getFormattedCommandsList() {
        String result = commands.values().stream()
                .map(Command::getDescription).map(s -> "  " + s).collect(Collectors.joining("\n"));
        return "Список команд и их описание:" + "\n" + result;
    }

    public String getFormattedHistory() {
        AtomicInteger index = new AtomicInteger(1);
        String result = commandsHistory.stream().map(command -> "  " + index.getAndIncrement() + ". " + command)
                .collect(Collectors.joining("\n"));
        return "Последние 15 команд:" + "\n" + result;
    }

    public void addCommand(Command command) {
        // logger.debug("Регистрация новой команды: {}", command.getName());
        Field[] fields = command.getClass().getDeclaredFields();

        for (Field field: fields) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object toInject = resolveDependency(field.getType());
                if (toInject == null) {
                    continue;
                }
                field.set(command, toInject);
                // logger.debug("В команду {} внедрен {}", command.getName(), field.getType().getSimpleName());

            } catch (IllegalAccessException e) {
                // logger.error("Не удалось внедрить зависимость в поле {}", field.getName(), e);
            }
        }
        commands.put(command.getName(), command);
        // logger.info("Команда {} зарегистрирована", command.getName());
    }

    /**
     * Решение зависимости
     */
    private Object resolveDependency(Class<?> type) {
        return switch (type.getSimpleName()) {
            // case "CollectionManager" -> collectionManager;
            case "CommandManager" -> this;
            // case "FileManager" -> fileManager;
            case "InputReader" -> reader;
            default -> null;
        };
    }
}