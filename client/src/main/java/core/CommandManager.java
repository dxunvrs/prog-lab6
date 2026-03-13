package core;

import clientCommands.*;
import exceptions.EndOfExecutionException;
import exceptions.ScriptExecutionException;
import network.Request;
import network.RequestType;
import network.Response;
import network.ResponseType;

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

    private final InputReader reader;
    private ConnectionManager connectionManager;

    public CommandManager(InputReader reader) {
        this.reader = reader;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        syncCommands();
    }

    private void syncCommands() {
        Response response = connectionManager.sendAndReceive(new Request(RequestType.SYNC, "get_commands"));
        if (response.getType() != ResponseType.SYNC_DATA) {
            System.out.println("Не удалось синхронизировать команды с сервером");
            return;
        }
        response.getSyncData().forEach((name, commandDef) -> {
            switch (commandDef.commandType()) {
                case NO_ARGS -> addCommand(new NoArgsCommand(name, commandDef.description()));
                case STRING_ARG -> addCommand(new StringArgCommand(name, commandDef.description()));
                case INT_ARG -> addCommand(new IntArgCommand(name, commandDef.description()));
                case OBJECT_ARG -> addCommand(new ObjectArgCommand(name, commandDef.description()));
                case MIXED_ARGS -> addCommand(new MixedArgsCommand(name, commandDef.description()));
                default -> System.out.println("Неизвестный тип команды, регистрация не удалась");
            }
        });
    }

    public boolean executeCommand(String line) {
        String[] tokens = line.split(" ");
        Command command = commands.get(tokens[0]);
        if (command == null) {
            // logger.warn("Пользователь ввел некорректную команду {}", tokens[0]);
            syncCommands();
            command = commands.get(tokens[0]);
            if (command == null) {
                System.out.println("Команда " + tokens[0] + " не найдена");
                return true;
            }
        }
        if (command.getExpectArgs() != tokens.length-1) {
            // logger.warn("Пользователь ввел неверное количество аргументов {} для команды {}", tokens.length-1, command.getName());
            System.out.println("Ожидалось " + command.getExpectArgs() + " аргументов, получено " + (tokens.length-1));
            return true;
        }
        try {
            // logger.debug("Начало выполнения команды {}", command.getName());

            Request request = command.execute(tokens);

            if (request.getType() == RequestType.CLIENT_COMMAND) {
                System.out.println(request.getCommandName());
                return true;
            }

            Response response = connectionManager.sendAndReceive(request);

            if (response.getType() == ResponseType.OUTDATED) syncCommands();

            System.out.println(response.getMessage() + " " + response.getType());
            addCommandToHistory(command.getName());

            return true;
        } catch (ScriptExecutionException e) {
            return notifyError("Ошибка выполнения скрипта: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            return notifyError("Неверный формат числа", e);
        } catch (EndOfExecutionException e) {
            // logger.info("Завершение программы", e);
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean notifyError(String message, Exception e) {
        // logger.error(e.getMessage(), e);
        System.out.println(message);
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
            case "CommandManager" -> this;
            case "InputReader" -> reader;
            default -> null;
        };
    }
}