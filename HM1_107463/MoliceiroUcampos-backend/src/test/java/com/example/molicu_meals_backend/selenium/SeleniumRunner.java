package com.example.molicu_meals_backend.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import java.time.Duration;

public class SeleniumRunner {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        try {
            driver.get("http://localhost:8080");

            // Clica no menu "Reservar"
            WebElement reservarLink = driver.findElement(By.linkText("Reservar"));
            reservarLink.click();

            // Espera carregar a página de /book
            Thread.sleep(1000);

            // Tenta encontrar o date picker (exemplo: <input type="date" id="date-picker">)
            WebElement datePicker = driver.findElement(By.cssSelector("input[type='date']"));
            datePicker.sendKeys("2025-04-10");

            Thread.sleep(500);

            // Tenta encontrar e clicar no botão "Reservar"
            WebElement reservarButton = driver.findElement(By.xpath("//button[contains(text(),'Reservar')]"));
            reservarButton.click();

            System.out.println("✅ Teste: tentativa de reserva feita com sucesso!");

        } catch (Exception e) {
            System.out.println("❌ Erro durante o teste Selenium:");
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
