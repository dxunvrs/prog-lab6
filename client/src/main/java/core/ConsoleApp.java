package core;

import clientCommands.ExitCommand;
import clientCommands.HelpCommand;
import clientCommands.HistoryCommand;
import exceptions.EndOfInputException;

import java.net.SocketException;

public class ConsoleApp {
    private final InputReader inputReader = new InputReader();
    private final CommandManager commandManager = new CommandManager(inputReader);

    private boolean isWorking = true;

    public ConsoleApp(String[] args) {
        // checkArgs(args);
        // registerAllCommands();
        registerBaseCommands();
        try {
            ConnectionManager connectionManager = new ConnectionManager("127.0.0.1", 1234);
            commandManager.setConnectionManager(connectionManager);
            System.out.println("Сервер подключен");
        } catch (SocketException e) {
            System.out.println("Сервер недоступен");
        }
    }

    public void interactive() {
        System.out.println("Ожидание ввода команды, для списка доступных команд - help");
        while (isWorking) {
            try {
                String line = inputReader.readNextLine("> ");
                String formattedLine = line.trim().replaceAll("\\s+", " ");

                if (inputReader.isScriptMode()) System.out.println(formattedLine); // для режима скрипта

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

    private void registerBaseCommands() {
        commandManager.addCommand(new HelpCommand());
        commandManager.addCommand(new ExitCommand());
        commandManager.addCommand(new HistoryCommand());
    }

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