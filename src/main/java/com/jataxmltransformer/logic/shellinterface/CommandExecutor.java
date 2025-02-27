package com.jataxmltransformer.logic.shellinterface;

import java.io.IOException;
import java.util.List;

/**
 * The {@code CommandExecutor} interface provides a contract for executing system or shell commands.
 * Any class implementing this interface should provide the implementation for executing a command
 * and returning its output as a {@link String}.
 *
 * <p>This interface is designed to allow flexible implementation, enabling different strategies
 * for executing commands (e.g., using {@link ProcessBuilder}, Java Native Interface (JNI), or other methods).</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *   CommandExecutor executor = new ProcessExecutor();
 *   String output = executor.execute(Arrays.asList("ls", "-l"));
 *   System.out.println(output);
 * </pre>
 */
public interface CommandExecutor {

    /**
     * Executes a system or shell command and returns the output as a {@link String}.
     *
     * <p>This method defines the basic contract for executing a command. It should handle
     * starting the command, reading its output, and returning the result as a string.
     * The method also needs to handle any exceptions that may arise during the execution process,
     * such as I/O errors or interruption of the process.</p>
     *
     * @param command a list of strings representing the command to execute.
     *                The first element is the command itself (e.g., "ls" or "cmd"),
     *                followed by any arguments (e.g., "-l" or "/c dir").
     * @return the output of the command execution as a {@link String}.
     * @throws IOException if an I/O error occurs during the command execution or reading the output.
     * @throws InterruptedException if the current thread is interrupted while waiting for the process to complete.
     */
    String execute(List<String> command) throws IOException, InterruptedException;
}