package com.jataxmltransformer.logic.shellinterface;

import java.io.IOException;
import java.util.List;

/**
 * The {@code CommandExecutor} interface defines the contract for executing system or shell commands
 * and capturing their output.
 * Any class that implements this interface should provide the logic to
 * execute a command and return the resulting output as a {@link String}.
 *
 * <p>This interface is designed to be flexible and can be implemented in different ways to support
 * various strategies for executing commands.
 * For instance, it can be implemented using the
 * {@link ProcessBuilder} class, the Java Native Interface (JNI), or any other appropriate method
 * for invoking system commands.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *   ProcessExecutorInterface executor = new ProcessExecutor();
 *   String output = executor.execute(Arrays.asList("ls", "-l"));
 *   System.out.println(output);
 * </pre>
 *
 * <p>By implementing this interface, the class allows for easy and consistent command execution
 * within the system or shell environment.</p>
 */
public interface ProcessExecutorInterface {

    /**
     * Executes a list of system or shell commands and returns the combined output as a {@link String}.
     *
     * <p>This method defines the essential contract for executing commands in a system or shell environment.
     * It is expected to handle starting the command, reading its output, and returning the output as a string.
     * The method should also ensure proper exception handling for common issues such as I/O errors
     * or interruption of the process.</p>
     *
     * <p>The provided list of commands is executed sequentially.
     * The first element of the list is
     * the command itself (e.g., "ls" or "cmd"), and the subsequent elements are the arguments to
     * the command (e.g., "-l" or "/c dir").
     * The commands are joined together into a single string,
     * separated by "&&" to ensure they are executed in sequence.</p>
     *
     * @param command a list of strings representing the command to execute, where the first element is
     *                the command itself and the following elements are arguments for the command.
     *                For example, a command list like {@code Arrays.asList("ls", "-l")} will execute
     *                the command {@code ls -l} in the shell.
     * @return the output of the executed command(s) as a {@link String}.
     * The result may include
     *         the standard output and any error messages produced during execution.
     * @throws IOException if an I/O error occurs while executing the command or reading its output.
     * @throws InterruptedException if the current thread is interrupted while waiting for the command
     *                              process to complete.
     *
     * @see ProcessBuilder
     * @see java.io.IOException
     */
    String execute(List<String> command) throws IOException, InterruptedException;
}