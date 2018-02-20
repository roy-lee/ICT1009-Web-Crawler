package com.webscrapper.selenium;

public class mainSpider {
	public static void main(String[] args)
    {
        System.setProperty("webdriver.chrome.driver" ,  "/Applications/chromedriver");
        redditDatabaseConnection.makeRedditConnection();
        Spider spider = new Spider();
        spider.search("https://www.reddit.com/r/singapore/", "");
    }
}