package com.jataxmltransformer.logs;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

/**
 * A utility class for logging application events at various levels (INFO, WARNING, SEVERE, etc.).
 * It supports logging to both a console and a file, using a configurable log directory.
 * <p>
 * This class creates a new log file on each application startup with a unique timestamp.
 * It maintains only the 10 most recent log files, deleting older ones automatically.
 */
public class AppLogger {

    private static final Logger logger = Logger.getLogger(AppLogger.class.getName());
    private static final int MAX_LOG_FILES = 10;
    private static final FileHandler fileHandler;

    // Static block for initializing logger settings
    static {
        try {
            Dotenv dotenv = Dotenv.load();
            String logDirectory = dotenv.get("LOG_DIRECTORY", "logs");
            Path logDirPath = Paths.get(logDirectory);

            // Create log directory if it doesn't exist
            if (!Files.exists(logDirPath)) {
                Files.createDirectories(logDirPath);
            }

            // Generate unique log file name with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String logFileName = String.format("app_%s.log", timestamp);
            Path logFilePath = logDirPath.resolve(logFileName);

            // Initialize file handler
            fileHandler = new FileHandler(logFilePath.toString(), true);

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

            // Clean up old log files
            cleanUpOldLogs(logDirPath);

            // Log initialization
            logger.info(String.format("Logging initialized. Log file: %s", logFilePath));

        } catch (IOException e) {
            throw new RuntimeException("Error while configuring log: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans up old log files, keeping only the MAX_LOG_FILES most recent ones.
     *
     * @param logDirPath Path to the log directory
     * @throws IOException If an I/O error occurs
     */
    private static void cleanUpOldLogs(Path logDirPath) throws IOException {
        List<Path> logFiles = Files.list(logDirPath)
                .filter(path -> path.toString().endsWith(".log"))
                .sorted(Comparator.comparingLong(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis();
                    } catch (IOException e) {
                        return 0;
                    }
                }))
                .toList();

        if (logFiles.size() > MAX_LOG_FILES) {
            int filesToDelete = logFiles.size() - MAX_LOG_FILES;
            for (int i = 0; i < filesToDelete; i++) {
                try {
                    Files.delete(logFiles.get(i));
                    logger.fine(String.format("Deleted old log file: %s", logFiles.get(i)));
                } catch (IOException e) {
                    logger.warning(String.format("Failed to delete old log file %s: %s",
                            logFiles.get(i), e.getMessage()));
                }
            }
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