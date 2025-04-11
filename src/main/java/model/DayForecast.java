package model;

import java.time.LocalDate;
import java.util.List;

public class DayForecast {
    private LocalDate date;
    private List<WeatherRecord> hourlyForecasts;

    public DayForecast(LocalDate date, List<WeatherRecord> hourlyForecasts) {
        this.date = date;
        this.hourlyForecasts = hourlyForecasts;
    }

    public LocalDate getDate() { return date; }
    public List<WeatherRecord> getHourlyForecasts() { return hourlyForecasts; }
}