package paulxyh.util.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private enum Levels {
        INFO, WARNING, ERROR
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void log(String message, Levels logLevel) {
        String timestamp = LocalDateTime.now().format(formatter);
        String output = "";

        switch (logLevel) {
            case INFO:
                output = String.format(
                        "[%s] [%s%s%s] %s",
                        timestamp,
                        LoggerConstants.LIGHT_BLUE,
                        "INFO",
                        LoggerConstants.RESET,
                        message
                );
                break;
            case WARNING:
                output = String.format(
                        "[%s] [%s%s%s] %s",
                        timestamp,
                        LoggerConstants.YELLOW,
                        "WARNING",
                        LoggerConstants.RESET,
                        message
                );
                break;
            case ERROR:
                output = String.format(
                        "[%s] [%s%s%s] %s",
                        timestamp,
                        LoggerConstants.RED,
                        "ERROR",
                        LoggerConstants.RESET,
                        message
                );
                break;
            default:
                break;
        }
        System.out.println(output);
    }

    public static void info(String message) {
        log(message, Levels.INFO);
    }

    public static void warn(String message){
        log(message, Levels.WARNING);
    }

    public static void error(String message){
        log(message, Levels.ERROR);
    }
}
