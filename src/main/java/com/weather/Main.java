package com.weather;

import java.util.Scanner;

public class Main {
    private static final String VERSION = "1.1.0";
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("=== Weather CLI v" + VERSION + "===");
        System.out.println("Type 'help' for available commands.\n");

        Scanner scanner = new Scanner(System.in);
        CommandHandler commandHandler = new CommandHandler();

        while (running) {
            System.out.print("weather> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                commandHandler.handle(input);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    public static void stop() {
        running = false;
    }
}
