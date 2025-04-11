package com.example.molicu_meals_backend.model;

public class WeatherResponse {
    private String description;
    private double temperature;
    private String icon;

    public WeatherResponse() {}

    public WeatherResponse(String description, double temperature, String icon) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
