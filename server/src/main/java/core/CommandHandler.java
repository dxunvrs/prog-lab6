package core;

import commands.Command;
import commands.Inject;

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

    /**
     * Метод для уведомления о некритичной ошибке
     * @param e класс ошибки
     * @return true для продолжения работы
     */
    private boolean notifyError(Exception e) {
        // logger.error(e.getMessage(), e);
        System.out.println(e.getMessage());
        return true;
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
        // commands.put(command.getName(), command);
        // logger.info("Команда {} зарегистрирована", command.getName());
    }

    private Object resolveDependency(Class<?> type) {
        return switch (type.getSimpleName()) {
            case "CollectionManager" -> collectionManager;
            case "CommandManager" -> this;
            default -> null;
        };
    }

//    public void startConsoleThread() {
//        Thread consoleThread = new Thread(() -> {
//            Scanner scanner = new Scanner(System.in);
//            while (!Thread.currentThread().isInterrupted()) {
//                if (scanner.hasNextLine()) {
//                    String line = scanner.nextLine().trim();
//                    if (line.equals("save")) {
//                        collectionManager.saveToFile(); // Сервер сохраняет коллекцию
//                        System.out.println("Коллекция успешно сохранена в файл.");
//                    } else if (line.equals("exit")) {
//                        collectionManager.saveToFile();
//                        System.out.println("Коллекция сохранена. Завершение работы...");
//                        System.exit(0);
//                    } else {
//                        System.out.println("Сервер поддерживает только команды: save, exit");
//                    }
//                }
//            }
//        });
//        consoleThread.setDaemon(true); // Чтобы поток не мешал закрытию программы
//        consoleThread.start();
//    }

//    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//        System.out.println("Emergency save...");
//        collectionManager.save();
//    }));
}