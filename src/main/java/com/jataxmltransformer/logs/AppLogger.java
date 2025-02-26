package com.jataxmltransformer.logs;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * A utility class for logging application events at various levels (INFO, WARNING, SEVERE, etc.).
 * It supports logging to both a console and a file, using a configurable log file path.
 * <p>
 * This class uses a custom log formatter that includes timestamps for each log entry.
 * Logging levels can be adjusted as needed, and the class prevents resource leaks by
 * allowing the file handler to be closed properly.
 */
public class AppLogger {

    private static final Logger logger = Logger.getLogger(AppLogger.class.getName());
    private static final FileHandler fileHandler;

    // Static block for initializing logger settings
    static {
        try {
            Dotenv dotenv = Dotenv.load();
            String logPath = dotenv.get("LOG_PATH");
            fileHandler = new FileHandler(logPath, true);

            // Apply custom formatter to handlers
            CustomFormatter formatter = new CustomFormatter();
            fileHandler.setFormatter(formatter);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);

            // Add handlers to logger
            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);

            // Set log levels for handlers and logger
            logger.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
            consoleHandler.setLevel(Level.ALL);

            // Disable default parent handlers
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            throw new RuntimeException("Error while configuring log: " + e.getMessage(), e);
        }
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param message The message to be logged.
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a message at the WARNING level.
     *
     * @param message The warning message to be logged.
     */
    public static void warning(String message) {
        logger.warning(message);
    }

    /**
     * Logs a message at the SEVERE level, typically for critical errors.
     *
     * @param message The severe error message to be logged.
     */
    public static void severe(String message) {
        logger.severe(message);
    }

    /**
     * Logs a fine-grained message, typically used for debugging purposes.
     *
     * @param message The detailed debug message to be logged.
     */
    public static void fine(String message) {
        logger.fine(message);
    }

    /**
     * Closes the file handler to release resources and prevent memory leaks.
     */
    public static void close() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    /**
     * A custom formatter for log messages. Includes the timestamp, log level, and message content.
     */
    private static class CustomFormatter extends Formatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            String timestamp = dateFormat.format(new Date(record.getMillis()));
            return String.format("[%s] [%s] %s%n",
                    timestamp,
                    record.getLevel(),
                    record.getMessage());
        }
    }
}
