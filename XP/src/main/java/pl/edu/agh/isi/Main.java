package pl.edu.agh.isi;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import pl.edu.agh.isi.cli.CreateTaskCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Command(
    name = "household-tasks",
    description = "Household Task Management Application",
    subcommands = {
        CreateTaskCommand.class
    },
    mixinStandardHelpOptions = true
)
public class Main implements Runnable {
    
    @Option(names = {"-i", "--interactive"}, description = "Run in interactive mode")
    private boolean interactive = false;
    
    public static void main(String[] args) {
        Main main = new Main();
        CommandLine cmd = new CommandLine(main);
        
        if (args.length > 0 && (args[0].equals("-i") || args[0].equals("--interactive"))) {
            main.runInteractive(cmd);
        } else {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        }
    }

    @Override
    public void run() {
        if (interactive) {
            runInteractive(new CommandLine(this));
        } else {
            // Show help if no subcommand is specified
            CommandLine.usage(this, System.out);
        }
    }
    
    private void runInteractive(CommandLine cmd) {
        System.out.println("Welcome to Household Task Management Application!");
        System.out.println("Type 'help' to see available commands or 'exit' to quit.");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        
        while (running) {
            try {
                System.out.print("> ");
                System.out.flush();
                String input = reader.readLine();
                
                if (input == null) {
                    // End of input stream
                    running = false;
                    continue;
                }
                
                input = input.trim();
                
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else if (input.equalsIgnoreCase("help")) {
                    cmd.usage(System.out);
                } else if (!input.isEmpty()) {
                    // Parse the input as command arguments
                    String[] cmdArgs = parseCommandLine(input);
                    cmd.execute(cmdArgs);
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                running = false;
            }
        }
    }
    
    private String[] parseCommandLine(String input) {
        // Simple command line parsing that respects quotes
        java.util.List<String> args = new java.util.ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : input.toCharArray()) {
            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                currentArg.append(c);
            }
        }
        
        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
        }
        
        return args.toArray(new String[0]);
    }
}