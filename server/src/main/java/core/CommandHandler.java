package core;

import commands.Command;
import commands.CommandDef;
import commands.Inject;
import exceptions.IdNotFoundException;
import network.Request;
import network.Response;
import network.ResponseType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    // private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private final Map<String, Command> commands = new HashMap<>();

    private final CollectionManager collectionManager;

    public CommandHandler(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response handle(Request request) {
        switch (request.getType()) {
            case SYNC -> {
                return syncCommands();
            }
            case SERVER_COMMAND -> {
                return executeCommand(request);
            }
            default -> {
                return new Response(ResponseType.ERROR, "Неизвестный тип запроса");
            }
        }
    }

    private Response executeCommand(Request request) {
        Command command = commands.get(request.getCommandName());
        if (command == null) {
            System.out.println("Команда не найдена");
            return new Response(ResponseType.OUTDATED, "Данная команда больше не поддерживается");
        }
        try {
            return command.execute(request);
        } catch (IdNotFoundException e) {
            return handleError(e);
        }
    }

    private Response syncCommands() {
        Map<String, CommandDef> commandDefMap = new HashMap<>();
        commands.forEach((name, serverCommand) ->
            commandDefMap.put(name, new CommandDef(serverCommand.getName(), serverCommand.getDescription(), serverCommand.getType()))
        );
        Response response = new Response(ResponseType.SYNC_DATA, "Актуальные команды");
        response.setSyncData(commandDefMap);
        return response;
    }

    private Response handleError(Exception e) {
        return new Response(ResponseType.ERROR, e.getMessage());
    }

//    public boolean executeCommand(String line) {
//        String[] tokens = line.split(" ");
//        Command command = commands.get(tokens[0]);
//        if (command == null) {
//            // logger.warn("Пользователь ввел некорректную команду {}", tokens[0]);
//            System.out.println("Команда " + tokens[0] + " не найдена");
//            return true;
//        }
//        if (command.getExpectArgs() != tokens.length-1) {
//            // logger.warn("Пользователь ввел неверное количество аргументов {} для команды {}", tokens.length-1, command.getName());
//            System.out.println("Ожидалось " + command.getExpectArgs() + " аргументов, получено " + (tokens.length-1));
//            return true;
//        }
//        try {
//            // logger.debug("Начало выполнения команды {}", command.getName());
//
//            ExecutionResponse executionResponse = command.execute(tokens);
//
//            System.out.println(executionResponse.message());
//
//            // logger.info("Сообщение команды: {}", executionResponse.message());
//            addCommandToHistory(command.getName());
//            // logger.debug("Команда {} добавлена в историю", command.getName());
//
//            return !executionResponse.shouldExit();
//        } catch (InvalidIdException | IdNotFoundException | SaveException | ScriptExecutionException e) {
//            return notifyError(e);
//        } catch (EndOfExecutionException e) {
//            // logger.info("Завершение программы", e);
//            System.out.println(e.getMessage());
//            return false;
//        }
//    }




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

    private Object resolveDependency(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "CollectionManager" -> collectionManager;
            case "CommandManager" -> this;
            default -> null;
        };
    }
}