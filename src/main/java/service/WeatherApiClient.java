package service;

import model.WeatherRecord;

import java.net.URLEncoder;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherApiClient {
    private static final String API_KEY = "f665ceada649571657d76aec5aea5396";
    private static final String GEOCODING_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/" +
            "forecast?lat=%s&lon=%s&appid=%s&lang=eng";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<WeatherRecord> fetchWeatherData(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        String geoUrl = String.format(GEOCODING_URL, encodedCity, API_KEY);
        JsonNode geoData = getJsonResponse(geoUrl);

        if (geoData.isEmpty()) {
            throw new IllegalArgumentException("City not found: " + city);
        }

        double lat = geoData.get(0).path("lat").asDouble();
        double lon = geoData.get(0).path("lon").asDouble();

        String forecastUrl = String.format(FORECAST_URL, lat, lon, API_KEY);
        JsonNode forecastData = getJsonResponse(forecastUrl);

        return processForecast(forecastData, city);
    }

    private JsonNode getJsonResponse(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }

    private List<WeatherRecord> processForecast(JsonNode root, String city) {
        List<WeatherRecord> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        JsonNode list = root.path("list");
        for (JsonNode item : list) {
            WeatherRecord record = new WeatherRecord();
            String dtText = item.path("dt_txt").asText();
            double tempKelvin = item.path("main").path("temp").asDouble();
            double tempCelsius = tempKelvin - 273.15;
            double roundedTemp = Math.round(tempCelsius * 2) / 2.0;

            record.setObservationDateTime(LocalDateTime.parse(dtText, formatter));
            record.setLocation(city);
            record.setTemperature(roundedTemp);
            record.setPrecipitation(item.path("weather").get(0).path("main").asText()
                    .equalsIgnoreCase("Rain"));
            record.setWeatherCondition(item.path("weather").get(0).path("description").asText());
            record.setWeatherIcon(item.path("weather").get(0).path("icon").asText());

            records.add(record);
        }
        return records;
    }
}