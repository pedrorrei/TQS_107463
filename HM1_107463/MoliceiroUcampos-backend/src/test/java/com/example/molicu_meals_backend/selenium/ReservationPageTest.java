package com.example.molicu_meals_backend.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ReservationPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    


    @Test
    public void shouldDisplayReservationDetailsForValidToken() {
        driver.get("http://localhost:5173/check-reservation");
    
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input")));
        input.sendKeys("0687BB");
    
        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Consultar')]")));
        checkBtn.click();
    
        WebElement detailsTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(), 'Detalhes da Reserva')]")));
        assertTrue(detailsTitle.isDisplayed(), "O título 'Detalhes da Reserva' deve estar visível.");
    
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table")));
        String tableText = table.getText().toLowerCase();
        assertTrue(tableText.contains("restaurante") || tableText.contains("0687bb"));
    }
    

    @Test
    public void shouldCancelReservation() {
        driver.get("http://localhost:5173/check-reservation");
    
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input")));
        input.sendKeys("07E19A");
    
        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Consultar')]")));
        checkBtn.click();
    
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Cancelar Reserva')]")));
        cancelBtn.click();
    
        WebElement confirmModalBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@id='confirmDeleteModal']//button[contains(text(), 'Confirmar')]")));
        confirmModalBtn.click();
    
        WebElement feedback = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".toast-message, .alert-success, .alert")));
        
        String text = feedback.getText().toLowerCase();
        assertTrue(text.contains("cancelada") || text.contains("sucesso"));
    }
    
    

}
