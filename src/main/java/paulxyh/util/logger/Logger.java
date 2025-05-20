package paulxyh.util.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level {
        DEBUG, INFO, WARNING, ERROR,
    }

    private static Level logLevel = Level.INFO;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void setLogLevel(Level level) {
        logLevel = level;
    }

    private static String getLogLevelColor(Level logLevel) {
        return switch (logLevel) {
            case INFO -> LoggerConstants.LIGHT_BLUE;
            case WARNING -> LoggerConstants.YELLOW;
            case ERROR -> LoggerConstants.RED;
            case DEBUG -> LoggerConstants.GRAY;
        };
    }

    private static boolean shouldLog(Level level) {
        return level.ordinal() >= logLevel.ordinal();
    }

    private static void log(String message, Level logLevel) {
        if(shouldLog(logLevel)) {
            String timestamp = LocalDateTime.now().format(formatter);
            String color = getLogLevelColor(logLevel);
            String level = logLevel.name();
            String output = String.format(
                    "[%s] [%s%s%s] %s",
                    timestamp,
                    color,
                    level,
                    LoggerConstants.RESET,
                    message
            );
            System.out.println(output);
        }
    }

    public static void info(String message) {
        log(message, Level.INFO);
    }

    public static void warn(String message) {
        log(message, Level.WARNING);
    }

    public static void error(String message) {
        log(message, Level.ERROR);
    }

    public static void debug(String message) {log(message, Level.DEBUG);}
}
