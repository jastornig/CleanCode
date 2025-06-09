package paulxyh.util.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Logger Tests")
public class LoggerTest {
    private final PrintStream standardOutput = System.out;
    private final ByteArrayOutputStream captureLogs = new ByteArrayOutputStream();

    @BeforeEach
    void init() {
        System.setOut(new PrintStream(captureLogs));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOutput);
    }

    @Test
    @DisplayName("Logger.info() should have correct formatting")
    void testLogInfoHasCorrectFormatting() {
        Logger.info("This is a message on log level info");

        String out = captureLogs.toString().trim();
        assertTrue(out.contains("INFO"));
        assertTrue(out.contains(LoggerConstants.LIGHT_BLUE));
        assertTrue(out.contains("This is a message on log level info"));
    }

    @Test
    @DisplayName("Logger.warn() should have correct formatting")
    void testLogWarnHasCorrectFormatting() {
        Logger.warn("This is a message on log level warn");

        String out = captureLogs.toString().trim();
        assertTrue(out.contains("WARN"));
        assertTrue(out.contains(LoggerConstants.YELLOW));
        assertTrue(out.contains("This is a message on log level warn"));
    }

    @Test
    @DisplayName("Logger.debug() should have correct formatting")
    void testLogDebugHasCorrectFormatting() {
        Logger.setLogLevel(Logger.Level.DEBUG); // Ensure debug level is enabled
        Logger.debug("This is a message on log level debug");

        String out = captureLogs.toString().trim();
        assertTrue(out.contains("DEBUG"));
        assertTrue(out.contains(LoggerConstants.GRAY));
        assertTrue(out.contains("This is a message on log level debug"));
    }

    @Test
    @DisplayName("Logger.error() should have correct formatting")
    void testLogErrorHasCorrectFormatting() {
        Logger.error("This is a message on log level error");

        String out = captureLogs.toString().trim();
        assertTrue(out.contains("ERROR"));
        assertTrue(out.contains(LoggerConstants.RED));
        assertTrue(out.contains("This is a message on log level error"));
    }

    @Test
    @DisplayName("getLogLevelColor() should return correct color code")
    void testGetLogLevelColorReturnsCorrectColorCode() throws IOException {
        Logger.setLogLevel(Logger.Level.DEBUG);
        Logger.info("Should return LIGHT_BLUE");
        String out = captureLogs.toString().trim();
        assertTrue(out.contains(LoggerConstants.LIGHT_BLUE));
        assertFalse(out.contains(LoggerConstants.YELLOW));
        assertFalse(out.contains(LoggerConstants.RED));
        assertFalse(out.contains(LoggerConstants.GRAY));
        captureLogs.reset();

        Logger.warn("Should return YELLOW");
        out = captureLogs.toString().trim();
        assertFalse(out.contains(LoggerConstants.LIGHT_BLUE));
        assertTrue(out.contains(LoggerConstants.YELLOW));
        assertFalse(out.contains(LoggerConstants.RED));
        assertFalse(out.contains(LoggerConstants.GRAY));
        captureLogs.reset();
        
        Logger.error("Should return RED");
        out = captureLogs.toString().trim();
        assertFalse(out.contains(LoggerConstants.LIGHT_BLUE));
        assertFalse(out.contains(LoggerConstants.YELLOW));
        assertTrue(out.contains(LoggerConstants.RED));
        assertFalse(out.contains(LoggerConstants.GRAY));
        captureLogs.reset();

        Logger.debug("Should return GRAY");
        out = captureLogs.toString().trim();
        assertFalse(out.contains(LoggerConstants.LIGHT_BLUE));
        assertFalse(out.contains(LoggerConstants.YELLOW));
        assertFalse(out.contains(LoggerConstants.RED));
        assertTrue(out.contains(LoggerConstants.GRAY));
    }
    @Test
    @DisplayName("Debug messages should not be logged when log level is set to INFO")
    void testLogLevel() {
        Logger.setLogLevel(Logger.Level.INFO);
        Logger.debug("This debug message should not be logged");
        String out = captureLogs.toString().trim();
        assertFalse(out.contains("DEBUG"));

        Logger.info("This info message should be logged");
        out = captureLogs.toString().trim();
        assertTrue(out.contains("INFO"));
    }

    @Test
    @DisplayName("Logger.info() should have correct formatting")
    void testTimestampHasCorrectFormatting() {
        Logger.info("Test timestamp format");
        String out = captureLogs.toString().trim();
        assertTrue(out.matches("^\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\].*"));
    }

}
