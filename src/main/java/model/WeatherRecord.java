package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class WeatherRecord {
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime observationDateTime;
    private String location;
    private double temperature;
    private boolean precipitation;
    private String weatherCondition;
    private String weatherIcon;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDateTime getObservationDateTime() { return observationDateTime; }
    public void setObservationDateTime(LocalDateTime observationDateTime) {
        this.observationDateTime = observationDateTime;
    }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public boolean isPrecipitation() { return precipitation; }
    public void setPrecipitation(boolean precipitation) { this.precipitation = precipitation; }
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }
}