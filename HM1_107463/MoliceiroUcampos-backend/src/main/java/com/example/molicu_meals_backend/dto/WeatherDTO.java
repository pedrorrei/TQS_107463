package com.example.molicu_meals_backend.dto;

public class WeatherDTO {
    private String description;
    private double temperature;
    private String icon;

    public WeatherDTO() {
    }

    public WeatherDTO(String description, double temperature, String icon) {
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }
}
