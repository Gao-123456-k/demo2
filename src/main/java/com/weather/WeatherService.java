package com.weather;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherService {
    private static final Logger LOGGER = Logger.getLogger(WeatherService.class.getName());
    private static final String GEO_API = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String WEATHER_API = "https://api.open-meteo.com/v1/forecast";

    public boolean isConfigured() {
        return true; // Open-Meteo doesn't need API key
    }

    public String getWeather(String city) throws WeatherException {
        try {
            // Step 1: Get coordinates for the city
            LOGGER.info("Searching for city: " + city);
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String geoUrl = GEO_API + "?name=" + encodedCity + "&count=1&language=zh";
            String geoResponse = httpGet(geoUrl);

            JsonObject geoJson = JsonParser.parseString(geoResponse).getAsJsonObject();
            if (!geoJson.has("results") || geoJson.getAsJsonArray("results").size() == 0) {
                throw new WeatherException("City not found: " + city);
            }

            JsonObject result = geoJson.getAsJsonArray("results").get(0).getAsJsonObject();
            double lat = result.get("latitude").getAsDouble();
            double lon = result.get("longitude").getAsDouble();
            String cityName = result.has("name") ? result.get("name").getAsString() : city;
            String country = result.has("country") ? result.get("country").getAsString() : "";

            // Step 2: Get weather data
            LOGGER.info("Fetching weather for " + cityName + " (" + lat + ", " + lon + ")");
            String weatherUrl = WEATHER_API + "?latitude=" + lat + "&longitude=" + lon
                    + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m"
                    + "&timezone=auto";
            String weatherResponse = httpGet(weatherUrl);

            JsonObject weatherJson = JsonParser.parseString(weatherResponse).getAsJsonObject();
            JsonObject current = weatherJson.getAsJsonObject("current");

            double temp = current.get("temperature_2m").getAsDouble();
            double feelsLike = current.get("apparent_temperature").getAsDouble();
            int humidity = current.get("relative_humidity_2m").getAsInt();
            double windSpeed = current.get("wind_speed_10m").getAsDouble();
            int weatherCode = current.get("weather_code").getAsInt();

            String description = getWeatherDescription(weatherCode);

            // Build result
            StringBuilder sb = new StringBuilder();
            sb.append("Weather in ").append(cityName);
            if (!country.isEmpty()) {
                sb.append(", ").append(country);
            }
            sb.append(":\n");
            sb.append("  Description: ").append(description).append("\n");
            sb.append("  Temperature: ").append(String.format("%.1f", temp)).append("°C\n");
            sb.append("  Feels Like: ").append(String.format("%.1f", feelsLike)).append("°C\n");
            sb.append("  Humidity: ").append(humidity).append("%\n");
            sb.append("  Wind Speed: ").append(String.format("%.1f", windSpeed)).append(" km/h");

            return sb.toString();

        } catch (WeatherException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching weather data", e);
            throw new WeatherException("Failed to fetch weather data: " + e.getMessage());
        }
    }

    private String httpGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new WeatherException("API request failed with code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    public String getForecast(String city, int days) throws WeatherException {
        try {
            // Step 1: Get coordinates for the city
            LOGGER.info("Searching for city: " + city);
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String geoUrl = GEO_API + "?name=" + encodedCity + "&count=1&language=zh";
            String geoResponse = httpGet(geoUrl);

            JsonObject geoJson = JsonParser.parseString(geoResponse).getAsJsonObject();
            if (!geoJson.has("results") || geoJson.getAsJsonArray("results").size() == 0) {
                throw new WeatherException("City not found: " + city);
            }

            JsonObject result = geoJson.getAsJsonArray("results").get(0).getAsJsonObject();
            double lat = result.get("latitude").getAsDouble();
            double lon = result.get("longitude").getAsDouble();
            String cityName = result.has("name") ? result.get("name").getAsString() : city;
            String country = result.has("country") ? result.get("country").getAsString() : "";

            // Step 2: Get forecast data
            LOGGER.info("Fetching " + days + "-day forecast for " + cityName);
            String weatherUrl = WEATHER_API + "?latitude=" + lat + "&longitude=" + lon
                    + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max"
                    + "&timezone=auto&forecast_days=" + days;
            String weatherResponse = httpGet(weatherUrl);

            JsonObject weatherJson = JsonParser.parseString(weatherResponse).getAsJsonObject();
            JsonObject daily = weatherJson.getAsJsonObject("daily");

            // Parse arrays
            var dates = daily.getAsJsonArray("time");
            var weatherCodes = daily.getAsJsonArray("weather_code");
            var tempMax = daily.getAsJsonArray("temperature_2m_max");
            var tempMin = daily.getAsJsonArray("temperature_2m_min");
            var precipitation = daily.getAsJsonArray("precipitation_sum");
            var windSpeed = daily.getAsJsonArray("wind_speed_10m_max");

            // Build result
            StringBuilder sb = new StringBuilder();
            sb.append(days).append("-Day Forecast for ").append(cityName);
            if (!country.isEmpty()) {
                sb.append(", ").append(country);
            }
            sb.append(":\n");
            sb.append(String.format("%-12s %-15s %8s %8s %10s %10s\n",
                    "Date", "Weather", "High", "Low", "Rain(mm)", "Wind(km/h)"));
            sb.append("-".repeat(75)).append("\n");

            for (int i = 0; i < dates.size(); i++) {
                String date = dates.get(i).getAsString();
                int code = weatherCodes.get(i).getAsInt();
                double high = tempMax.get(i).getAsDouble();
                double low = tempMin.get(i).getAsDouble();
                double rain = precipitation.get(i).getAsDouble();
                double wind = windSpeed.get(i).getAsDouble();
                String description = getWeatherDescription(code);

                sb.append(String.format("%-12s %-15s %7.1f°C %7.1f°C %8.1fmm %8.1f\n",
                        date, description, high, low, rain, wind));
            }

            return sb.toString();

        } catch (WeatherException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching forecast data", e);
            throw new WeatherException("Failed to fetch forecast data: " + e.getMessage());
        }
    }

    private String getWeatherDescription(int code) {
        // WMO Weather interpretation codes
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code <= 49) return "Foggy";
        if (code <= 59) return "Drizzle";
        if (code <= 69) return "Rain";
        if (code <= 79) return "Snow";
        if (code <= 82) return "Rain showers";
        if (code <= 85) return "Snow showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }
}
