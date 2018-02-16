package com.webscrapper.selenium;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomePage {

    public static void main(String[] args) throws IOException,InterruptedException {
        // Sets chromedriver path
        System.setProperty("webdriver.chrome.driver" ,  "/Applications/chromedriver");

        DatabaseConnection.makeJDBCConnection();
        int taskCounter = 0;

        final TimerTask task = new TimerTask() {
            int taskCounter = 0;
            public void run() {
                Date d = new Date();
                System.out.println("********************");
                System.out.println("Running scheduled task ran at " + d);
                System.out.println("********************");

                //Facebook - TODAY News
                SiteInfo facebook = new SiteInfo("https://www.facebook.com/pg/todayonline/posts/", "div.userContent", "span.timestampContent");
                System.out.println(SiteInfo.getSiteInfo());
                Scrapper.scrape();


                //Twitter - SG News
                SiteInfo twitter = new SiteInfo("https://twitter.com/sgnews", "p.tweet-text", "span._timestamp");
                System.out.println(SiteInfo.getSiteInfo());
                Scrapper.scrape();

                taskCounter += 1;
                System.out.println("********************");
                System.out.println("Scheduled task was run for " + taskCounter + " times.");
                System.out.println("********************");
                System.out.println("\n");
            }
        };

        Timer timer = new Timer();

        long delay = 100; // sets delay to run task
        long period = 120000; // sets interval between scheduled tasks [60000ms = 1min]
        timer.scheduleAtFixedRate(task, delay, period);;

    }
}