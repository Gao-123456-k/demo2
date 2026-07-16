package com.weather;

public class CommandHandler {
    private final WeatherService weatherService;

    public CommandHandler() {
        this.weatherService = new WeatherService();
    }

    public void handle(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "help":
                showHelp();
                break;
            case "version":
                showVersion();
                break;
            case "status":
                showStatus();
                break;
            case "weather":
                handleWeather(argument);
                break;
            case "forecast":
                handleForecast(argument);
                break;
            case "exit":
            case "quit":
                Main.stop();
                break;
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Type 'help' for available commands.");
        }
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help     - Show this help message");
        System.out.println("  version  - Show version information");
        System.out.println("  status   - Show program status");
        System.out.println("  weather <city> - Query current weather for a city");
        System.out.println("  forecast <city> [days] - Query weather forecast (default: 7 days)");
        System.out.println("  exit     - Exit the program");
    }

    private void showVersion() {
        System.out.println("Weather CLI v1.1.0");
    }

    private void showStatus() {
        System.out.println("Program Status:");
        System.out.println("  Running: Yes");
        System.out.println("  Weather API: " + (weatherService.isConfigured() ? "Configured" : "Not configured"));
        System.out.println("  Java Version: " + System.getProperty("java.version"));
    }

    private void handleWeather(String city) {
        if (city.isEmpty()) {
            System.err.println("Error: Please specify a city name.");
            System.out.println("Usage: weather <city>");
            return;
        }

        try {
            String result = weatherService.getWeather(city);
            System.out.println(result);
        } catch (WeatherException e) {
            System.err.println("Weather Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private void handleForecast(String args) {
        if (args.isEmpty()) {
            System.err.println("Error: Please specify a city name.");
            System.out.println("Usage: forecast <city> [days]");
            return;
        }

        String[] parts = args.split("\\s+", 2);
        String city = parts[0];
        int days = 7; // default

        if (parts.length > 1) {
            try {
                days = Integer.parseInt(parts[1]);
                if (days < 1 || days > 16) {
                    System.err.println("Error: Days must be between 1 and 16.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid number of days.");
                return;
            }
        }

        try {
            String result = weatherService.getForecast(city, days);
            System.out.println(result);
        } catch (WeatherException e) {
            System.err.println("Forecast Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
