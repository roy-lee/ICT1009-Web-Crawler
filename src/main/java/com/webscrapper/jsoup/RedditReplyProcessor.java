package com.webscrapper.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
//------------------------------------Responsible for scraping comment--------------------------------------------------

public class RedditReplyProcessor {
    public static ArrayList<String> retrieveRedditReplies(String url) {
        ArrayList<String> redditReplies = new ArrayList<>();

        for(String redditReply : redditReplies) {
            System.out.println(String.format("[REPLY] Parsing reply: %s", redditReply));
        }
        try {
            //-----------------------Get the replies from a reddit topic------------------------------------------------
            Document redditDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")
                    .referrer("https://www.google.com.au")
                    .header("DNT","1")
                    .get();
            Elements rawReplyElements = redditDoc.getElementsByClass("md").select("p");

            for(Element element : rawReplyElements) {
                //SKIPPING USELESS LINKS WITHIN COMMENTS
                if (!element.toString().contains("a href=")) {
                    redditReplies.add(element.html());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return redditReplies;
    }
}