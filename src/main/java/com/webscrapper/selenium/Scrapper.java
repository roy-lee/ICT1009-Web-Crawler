package com.webscrapper.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Scrapper {


    public static void scrape() {

//		Mac OS Chrome Driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // scrape in the background without showing browser
        WebDriver site = new ChromeDriver(options);

        String siteName = SiteInfo.getSiteName();
        String dataTitle = SiteInfo.getSiteDataTitle();
        String dataTime = SiteInfo.getSiteDataTime();
        int dataID = 0;

        site.navigate().to(siteName);

        //findElement would throw a NoSuchElement exception if the element is not present. Using findElements is a better approach
        List<WebElement> titles = site.findElements(By.cssSelector(dataTitle));
        List<WebElement> dates = site.findElements(By.cssSelector(dataTime));

        // Get the title of the page
        System.out.println("Now scraping: " + site.getTitle());
        System.out.println("Please wait...");

        // Let page load for 5 seconds
        long timer = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < timer) {
            // Scroll page down
            site.findElement(By.tagName("body")).sendKeys(Keys.END);
        }

        System.out.println(" =============== " + site.getTitle() + " ================= ");

        // Get latest dataID in database
        try {
            dataID = DatabaseConnection.getLastID();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataID += 1;

        // Get current date and time
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        int numberOfData = 0;
        try {
            for (int j = 0; j < titles.size(); j++) {
                // CHECK IF CURRENT TITLE EXIST IN DATABASE
                try {
                    if (DatabaseConnection.eixistingTitleChecker(titles.get(j).getText()) != false) {
                        System.out.println(dates.get(j).getText() + "\t - " + titles.get(j).getText());
                        DatabaseConnection.addDataToDB(dataID,titles.get(j).getText(),"00:00:00.000000",site.getTitle(),timeStamp);
                        dataID += 1;
                        numberOfData += 1;
                    } else {
                        break;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception thrown:"+ e);
        }

        if (numberOfData == 0) {
            System.out.println("No new data since last scrape.");
        } else {
            System.out.println("Scrapped " + numberOfData + " data.");
        }

        System.out.println("\n");

        //Close the browser
        site.close();
    }
}
