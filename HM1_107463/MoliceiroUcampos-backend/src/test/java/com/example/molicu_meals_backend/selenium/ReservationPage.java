package com.example.molicu_meals_backend.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReservationPage {

    private final WebDriver driver;

    public ReservationPage(WebDriver driver) {
        this.driver = driver;
    }

    public void setDate(String date) {
        WebElement dateInput = driver.findElement(By.cssSelector("input[type='date']"));
        dateInput.sendKeys(date);
    }

    public void selectAvailableMenu() {
        WebElement menuSelect = driver.findElement(By.cssSelector("select.form-select"));
        menuSelect.click();
        WebElement option = driver.findElement(By.xpath("//option[not(@disabled)]"));
        option.click();
    }

    public void submitReservation() {
        WebElement confirmBtn = driver.findElement(By.cssSelector("button.btn-success"));
        confirmBtn.click();
    }

    public String getSuccessMessage() {
        return driver.findElement(By.className("alert-success")).getText();
    }

    public String getErrorMessage() {
        return driver.findElement(By.id("errorModal")).getText();
    }
} 
