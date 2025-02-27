package com.jataxmltransformer.logic.shellinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * The {@code ProcessExecutor} class provides an implementation of the {@link CommandExecutor} interface.
 * This class is responsible for executing system commands or shell commands through the {@link ProcessBuilder} class.
 * It captures the output of the executed command and returns it as a {@link String}.
 * In case of an error, it throws a {@link RuntimeException} with the exit code and error output.
 *
 * <p>This implementation can be used to run shell commands on both Linux and Windows platforms.
 * It captures standard output and standard error streams and combines them into a single output.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *   ProcessExecutor executor = new ProcessExecutor();
 *   String output = executor.execute(Arrays.asList("ls", "-l"));
 *   System.out.println(output);
 * </pre>
 */
public class ProcessExecutor implements CommandExecutor {

    /**
     * Executes a command in the system shell and returns the output as a {@link String}.
     *
     * <p>The method creates a {@link ProcessBuilder} to run the command, captures the output
     * from the standard output stream, and returns it as a string. If the command fails (i.e.,
     * returns a non-zero exit code), a {@link RuntimeException} is thrown with the exit code
     * and the error output.</p>
     *
     * @param command a list of strings representing the command and its arguments to be executed.
     *                The first element is the command itself (e.g., "ls" or "cmd"), followed by
     *                any arguments (e.g., "-l" or "/c dir").
     * @return the output of the command as a {@link String}.
     * @throws IOException if an I/O error occurs while starting or reading from the process.
     * @throws InterruptedException if the current thread is interrupted while waiting for the process to complete.
     * @throws RuntimeException if the command exits with a non-zero exit code, indicating failure.
     */
    @Override
    public String execute(List<String> command) throws IOException, InterruptedException {
        // Setting up the shell executor
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Getting the output of the execution of the command to be returned
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            output.append(line).append("\n");

        int exitCode = process.waitFor();
        if (exitCode != 0)
            throw new RuntimeException(process.exitValue() + ": " + output);

        return output.toString();
    }
}