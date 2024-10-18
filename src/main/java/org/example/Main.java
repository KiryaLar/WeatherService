package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    private static final String KEY = "some_key";

    public static void main(String[] args) {
        getWeatherData(55.7522, 37.6156, 20);
    }

    public static void getWeatherData(double lat, double lon, int limit) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon))
                    .header("X-Yandex-API-Key", KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Full JSON response: " + response.body());

            JSONObject jsonResponse = new JSONObject(response.body());
            JSONObject fact = jsonResponse.getJSONObject("fact");
            int currentTemp = fact.getInt("temp");
            System.out.println("Current temperature: " + currentTemp + "°C");

            JSONArray forecasts = jsonResponse.getJSONArray("forecasts");

            double averageTemp = calculateAverageTemperature(forecasts, limit);
            System.out.println("Average temperature for " + Math.min(forecasts.length(), limit) + " days: " + averageTemp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double calculateAverageTemperature(JSONArray forecasts, int limit) {

        double sumTemp = 0;
        int daysCount = Math.min(forecasts.length(), limit);
        if (limit > forecasts.length()) {
            System.out.println("Forecast is only available for " + forecasts.length() + " days");
        }
        for (int i = 0; i < daysCount; i++) {
            JSONObject forecast = forecasts.getJSONObject(i);
            JSONObject parts = forecast.getJSONObject("parts");
            JSONObject day = parts.getJSONObject("day");
            int dayTemp = day.getInt("temp_avg");
            sumTemp += dayTemp;
        }
        return sumTemp / daysCount;
    }
}