package core;

import exceptions.EndOfInputException;
import io.InputManager;

import java.net.SocketException;

public class ConsoleApp {
    private final CommandManager commandManager = new CommandManager();
    private final InputManager inputManager = new InputManager(commandManager::getCommandNames);

    private boolean isWorking = true;

    public ConsoleApp() {
        // checkArgs(args);
        // registerAllCommands();
        try {
            ConnectionManager connectionManager = new ConnectionManager("localhost", 1234);
            commandManager.setConnectionManager(connectionManager);
            commandManager.setInputManager(inputManager);
            commandManager.configure();
            // System.out.println("Сервер подключен");
        } catch (SocketException e) {
            System.out.println("Ошибка сокета: " + e.getMessage());
        }
    }

    public void interactive() {
        System.out.println("Ожидание ввода команды, для списка доступных команд - help");
        while (isWorking) {
            try {
                String line = inputManager.readNextLine("> ", true);
                String formattedLine = line.trim().replaceAll("\\s+", " ");

                if (inputManager.isScriptMode()) System.out.println(formattedLine); // для режима скрипта

                if (formattedLine.isEmpty()) continue;

                if (!commandManager.executeCommand(formattedLine)) {
                    isWorking = false;
                }
            } catch (EndOfInputException e) {
                System.out.println(e.getMessage());
                isWorking = false;
            }
        }
    }

//    private void registerBaseCommands() {
//        commandManager.addCommand(new HelpCommand());
//        commandManager.addCommand(new ExitCommand());
//        commandManager.addCommand(new HistoryCommand());
//        commandManager.addCommand(new ExecuteScriptCommand());
//    }

//    /**
//     * Проверка аргументов запуска программы
//     */
//    private void checkArgs(String[] args) {
//        if (args.length == 0) {
//            System.out.println("Имя файла с коллекцией не указано, создана новая коллекция");
//        } else {
//            if (args.length > 1) System.out.println("Указано больше одного аргумента, в качестве имени файла взят первый полученный аргумент");
//            fileManager.setFileName(args[0]);
//            fileManager.load(collectionManager);
//        }
//    }
//
//    /**
//     * Регистрация команд
//     */
//    private void registerAllCommands() {
//        commandManager.addCommand(new HelpCommand());
//        commandManager.addCommand(new InfoCommand());
//        commandManager.addCommand(new ExitCommand());
//        commandManager.addCommand(new AddCommand());
//        commandManager.addCommand(new ClearCommand());
//        commandManager.addCommand(new ShowCommand());
//        commandManager.addCommand(new UpdateCommand());
//        commandManager.addCommand(new RemoveCommand());
//        commandManager.addCommand(new HistoryCommand());
//        commandManager.addCommand(new SortCommand());
//        commandManager.addCommand(new ShuffleCommand());
//        commandManager.addCommand(new SumOfPriceCommand());
//        commandManager.addCommand(new AverageOfPriceCommand());
//        commandManager.addCommand(new FilterStartsWithNameCommand());
//        commandManager.addCommand(new SaveCommand());
//        commandManager.addCommand(new ExecuteScriptCommand());
//    }
}