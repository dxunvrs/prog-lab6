package core;

import exceptions.EndOfInputException;
import exceptions.ScriptExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class InputReader {
    private static class ScriptSource {
        Scanner scanner;
        String fileName;

        ScriptSource(Scanner scanner, String fileName) {
            this.scanner = scanner;
            this.fileName = fileName;
        }
    }
    // private final Logger logger = LoggerFactory.getLogger(InputReader.class);

    private final Set<String> pathHistory = new HashSet<>();

    private final Deque<ScriptSource> sourceDeque = new ArrayDeque<>();

    public InputReader() {
        sourceDeque.push(new ScriptSource(new Scanner(System.in), null));
    }

    public void enqueueScript(String fileName) throws IOException {
        if (pathHistory.contains(fileName)) {
            throw new ScriptExecutionException("Обнаружена рекурсия, файл: " + fileName);
        }
        sourceDeque.push(new ScriptSource(new Scanner(new File(fileName)),fileName));

        pathHistory.add(fileName);
        //logger.debug("В очередь добавлен новый скрипт {}", fileName);
    }

    public String readNextLine(String prompt) {
        while (!sourceDeque.isEmpty()) {
            System.out.print(prompt);

            ScriptSource currentSource = sourceDeque.peek();
            Scanner currentScanner = Objects.requireNonNull(currentSource).scanner;
            if (currentScanner.hasNextLine()) {
                return currentScanner.nextLine();
            }
            if (sourceDeque.size() > 1) {
                currentScanner.close();
                ScriptSource finishedSource = sourceDeque.pop();
                pathHistory.remove(finishedSource.fileName);

                //logger.info("Скрипт {} завершен, возвращение к предыдущему источнику", finishedSource.fileName);
                System.out.println("Конец выполнения скрипта " + finishedSource.fileName);
                continue;
            }
            sourceDeque.pop();
            throw new EndOfInputException("Конец ввода");
        }
        throw new EndOfInputException("Чтение из пустой очереди");
    }

    public boolean isScriptMode() {
        return sourceDeque.size() != 1;
    }
}