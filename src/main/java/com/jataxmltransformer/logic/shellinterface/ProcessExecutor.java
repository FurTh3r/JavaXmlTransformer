package com.jataxmltransformer.logic.shellinterface;

import com.jataxmltransformer.logs.AppLogger;

import java.io.*;
import java.util.List;

/**
 * Executes shell commands in a new process, captures the output, and returns the result.
 * The class uses the WSL (Windows Subsystem for Linux) for executing commands in a Linux environment.
 */
public class ProcessExecutor implements ProcessExecutorInterface {

    /**
     * Executes a list of commands in a new shell process, captures the output,
     * and returns the result as a string.
     *
     * @param command The list of commands to be executed in the shell.
     * @return The output of the executed commands.
     * @throws IOException If an I/O error occurs during the execution.
     * @throws InterruptedException If the execution is interrupted.
     */
    @Override
    public String execute(List<String> command) throws IOException, InterruptedException {
        // Start a new process for each command, using WSL as the shell
        ProcessBuilder processBuilder = new ProcessBuilder("wsl");
        processBuilder.redirectErrorStream(true); // Redirects error stream to standard output
        Process process = processBuilder.start();

        // Use BufferedWriter for sending commands and BufferedReader for capturing output
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            // Combine the commands into a single string, separated by '&&' to chain them
            String commandStr = String.join(" && ", command);
            writer.write(commandStr + "\n");
            writer.flush(); // Send the command to the shell

            // StringBuilder to store the output of the command execution
            StringBuilder output = new StringBuilder();
            String line;

            // Read each line of the output from the shell process
            while ((line = reader.readLine()) != null && !line.equals("EXIT")) { // NOTE: EXIT is a custom EOL!
                AppLogger.info("Output: " + line); // Print each line of output for debugging
                output.append(line).append("\n");
            }

            // Return the captured output as a string
            return output.toString();
        } catch (IOException e) {
            AppLogger.severe(e.getMessage());
            throw e; // Rethrow the exception after logging it
        } finally {
            process.destroy(); // Ensure the process is destroyed after execution
            AppLogger.info("Process destroyed. Terminated execution.");
        }
    }
}
