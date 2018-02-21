package com.webscrapper.selenium;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class mainSpider {
	public static void main(String[] args)
    {
        System.setProperty("webdriver.chrome.driver" ,  "/Applications/chromedriver");
        redditDatabaseConnection.makeRedditConnection();

        int taskCounter = 0;

        final TimerTask task = new TimerTask() {
            int taskCounter = 0;
            public void run() {
                Date d = new Date();
                System.out.println("********************");
                System.out.println("Running scheduled task ran at " + d);
                System.out.println("********************");

                //Facebook - TODAY News
                Spider spider = new Spider();
                spider.search("https://www.reddit.com/r/singapore/", "");

                taskCounter += 1;
                System.out.println("********************");
                System.out.println("Scheduled task was run for " + taskCounter + " times.");
                System.out.println("********************");
                System.out.println("\n");
            }
        };

        Timer timer = new Timer();

        long delay = 100; // sets delay to run task
        long period = 600000; // sets interval between scheduled tasks [60000 ms = 1min]
        timer.scheduleAtFixedRate(task, delay, period);;



    }
}

