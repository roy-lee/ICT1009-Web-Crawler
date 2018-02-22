package com.webscrapper.jsoup;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CrawlerSearch {
    private static final int MAX_PAGES = 5;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesLeftToVisit = new LinkedList<String>();
    private int currentPagesScraped = 0;

    // Starting point for the Scraper's functionality. It make an HTTP request and parse the response (the web page).
    public void search(String url)
    {
        // -------------------------------Add pages to be scraped to list-----------------------------------------------
        String currentUrl;
        Crawler crawler = new Crawler();
        // Add url if list is empty
        if(this.pagesLeftToVisit.isEmpty()) {
            currentUrl = url;
            this.pagesVisited.add(url);
        }
        // If not, go to the next url
        else {
            currentUrl = this.nextUrl();
        }
        //----------------------------Happens while pages searched is within search quota------------------------------------------
        while (currentPagesScraped < MAX_PAGES){
            crawler.crawl(currentUrl); // Method is in Crawler
            currentUrl = crawler.getNextURL(currentUrl);
            System.out.println("---------------------------------------------------------------To next page---------------------------------------------------------");
            currentPagesScraped++;
        }
        this.pagesLeftToVisit.addAll(crawler.retrieveLinks());
    }
    // Returns the next URL to visit (in the order that they were found). It also do a check to make sure this method doesn't return a URL that has already been visited.
    private String nextUrl()
    {
        String nextUrl;
        do
        {
            nextUrl = this.pagesLeftToVisit.remove(0);

        } while(this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        System.out.println("next url is: " + nextUrl);
        return nextUrl;
    }
}