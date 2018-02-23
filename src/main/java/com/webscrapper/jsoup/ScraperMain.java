package com.webscrapper.jsoup;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ScraperMain {
    public static void main(String[] args)
    {
        System.setProperty("webdriver.chrome.driver" ,  "/Applications/chromedriver");
        redditDatabaseConnection.makeRedditConnection();

        final TimerTask task = new TimerTask() {
            int taskCounter = 0;
            public void run() {
                Date d = new Date();
                System.out.println("********************");
                System.out.println("Running scheduled task ran at " + d);
                System.out.println("********************");

                //Reddit - Singapore
                CrawlerSearch spider = new CrawlerSearch();
                spider.search("https://www.reddit.com/r/singapore/");

                taskCounter += 1;
                System.out.println("********************");
                System.out.println("Scheduled task was run for " + taskCounter + " times.");
                System.out.println("********************");
                System.out.println("\n");
            }
        };

        Timer timer = new Timer();

        long delay = 100; // sets delay to run task
        long period = 300000; // sets interval between scheduled tasks [60000 ms = 1min]
        timer.scheduleAtFixedRate(task, delay, period);
    }
}