package com.webscrapper.selenium;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class SeleniumHQTest {

    @Test
    public void startWebDriver() {
        System.setProperty("webdriver.chrome.driver" ,  "/Applications/chromedriver");
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless"); // scrape in the background without showing browser
        WebDriver site = new ChromeDriver(options);

        site.navigate().to("https://www.google.com.sg");

        site.close();
        site.quit();
    }
}