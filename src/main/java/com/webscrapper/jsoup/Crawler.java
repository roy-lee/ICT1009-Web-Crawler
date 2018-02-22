package com.webscrapper.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.ac.wlv.sentistrength.SentiStrength;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    // Use a fake USER_AGENT so the web server thinks the crawler is a normal web browser
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDoc;
    private static final int MAX_THREADS = 25;

    //Establishes makes an HTTP request, checks for response, and then gathers up all the links on the page
    public boolean crawl (String url) {
        //Method 2: One initialisation and repeated classifications
        SentiStrength sentiStrength = new SentiStrength();
        //Create an array of command line parameters to send (not text or file to process)
        String ssthInitialisation[] = {"sentidata", "/Users/roylee/Downloads/SentiStrength/SentiStrength_DataEnglishFeb2017/", "trinary"};
        sentiStrength.initialise(ssthInitialisation); //Initialise
        //Trinary mode: -1 = negative, 0 = neutral, 1 = positive
        //can now calculate sentiment scores quickly without having to initialise again

        try {
            //----------------Established a connection to the web page, return false if failed--------------------------
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDoc = connection.get();
            this.htmlDoc = htmlDoc;

            //--------------------Connection to the page has been established successfully------------------------------
            if (connection.response().statusCode() == 200){ // 200 is the HTTP OK status code
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }

            //-----------------------------Filters out pages not in the scope-------------------------------------------
            Elements linksOnPage = htmlDoc.select("a[href]");
            String userInput = "https://www.reddit.com/r/singapore/comments/";//This is used for filtering
            String firstformatlink = "";

            for (int i = 0; i < 1; i++) {
                for (Element e : linksOnPage)   {//Foreach loop to get each element in linksOnPage
                    if (e.tagName("href").toString().contains(userInput)) {
                        String filteredLinks = e.toString();
                        firstformatlink = firstformatlink + filteredLinks;
                    }
                }
            }
            List FormattedUrls = Crawler.extractNewUrls(firstformatlink);
            //---------------------Remove excessive threads(Ex. pinned threads) before scraping-------------------------
            while(FormattedUrls.size() > 25){
                FormattedUrls.remove(0);
            }

            // Get latest postID in database
            int postID = 0;
            try {
                postID = redditDatabaseConnection.getLastID();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            postID += 1;

            // Get latest commentID in database
            int commentID = 0;
            try {
                commentID = redditDatabaseConnection.getLastCommentID();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            commentID += 1;

            //----------------------------Scrape page and print results-------------------------------------------------
            for (int i = 0; i < MAX_THREADS; i++) {
                String redditUrl = (String) FormattedUrls.get(i);

                RedditReplyProcessor redditHandler = new RedditReplyProcessor();
                ArrayList<String> redditReplies = redditHandler.retrieveRedditReplies(redditUrl);

                Connection redditPostPage = Jsoup.connect(redditUrl).userAgent(USER_AGENT);
                Document element = redditPostPage.get();
                Elements pageTitle = element.select("a.title");

                try {
                    redditReplies.remove(0);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                int numberOfComments= 0, tempPostID = 0;
                int positiveSentimentValue, posVal = 0;
                int negativeSentimentValue, negVal = 0;
                int neutralSentimentValue, neutralVal = 0;
                String dataSentiment = "";
                try {
                    tempPostID = postID;
                    if (redditDatabaseConnection.existingTitleChecker(pageTitle.text()) != false) {
                        System.out.println("-----------------------------New post------------------------------------");
                        redditDatabaseConnection.addDataToDB(postID,pageTitle.text().toString(),redditUrl,"");

                        postID += 1;
                    }

                    for(String redditReply : redditReplies)
                    {
                        try {
                            if (redditDatabaseConnection.existingCommentChecker(redditReply) != false) {

                                String emotion = sentiStrength.computeSentimentScores(redditReply);
                                String[] sentiAnalysis = emotion.split(" ", -1);

                                int commentPositiveSentiment = Integer.valueOf(sentiAnalysis[0]);
                                int commentNegativeSentiment = Math.abs(Integer.valueOf(sentiAnalysis[1]));
                                int commentNeutralSentiment = Math.abs(Integer.valueOf(sentiAnalysis[2]));

                                if (commentPositiveSentiment > commentNegativeSentiment && commentPositiveSentiment > commentNeutralSentiment) {
                                    dataSentiment = "Positive";
                                }

                                if (commentNegativeSentiment > commentPositiveSentiment && commentNegativeSentiment > commentNeutralSentiment) {
                                    dataSentiment = "Negative";
                                }

                                if (commentNeutralSentiment > commentNegativeSentiment && commentNeutralSentiment > commentPositiveSentiment || commentPositiveSentiment == commentNegativeSentiment) {
                                    dataSentiment = "Neutral";
                                }
                                redditDatabaseConnection.addCommentToDB(commentID,redditReply, pageTitle.text().toString(),dataSentiment);
                                commentID += 1;
                                numberOfComments+=1;
                            }
                            String emotion = sentiStrength.computeSentimentScores(redditReply);
                            String[] sentiAnalysis = emotion.split(" ", -1);

                            positiveSentimentValue = Integer.valueOf(sentiAnalysis[0]);
                            negativeSentimentValue = Math.abs(Integer.valueOf(sentiAnalysis[1]));
                            neutralSentimentValue = Math.abs(Integer.valueOf(sentiAnalysis[2]));

                            posVal += positiveSentimentValue;
                            negVal += negativeSentimentValue;
                            neutralVal += neutralSentimentValue;
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    System.out.println("Page title: " + pageTitle.text());
                    System.out.println(String.format("[URL] %s", redditUrl));

                } catch (SQLException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } finally {
                    if (posVal > negVal && posVal > neutralVal) {
                        dataSentiment = "Positive";
                    }

                    if (negVal > posVal && negVal > neutralVal) {
                        dataSentiment = "Negative";
                    }

                    if (neutralVal > negVal && neutralVal > posVal || posVal == negVal) {
                        dataSentiment = "Neutral";
                    }
                    redditDatabaseConnection.addSentimentToDB(tempPostID, dataSentiment);
                    tempPostID = postID;

                }

                if (numberOfComments == 0) {
                    System.out.println("No new comments since last scrape.");
                    System.out.println("Overall sentiment: " + dataSentiment);
                } else {
                    System.out.println("Scrapped " + numberOfComments + " comments.");
                    System.out.println(posVal + " positive points");
                    System.out.println(negVal + " negative points");
                    System.out.println(neutralVal + " neutral points");
                    System.out.println("Overall sentiment: " + dataSentiment);
                }
                System.out.println("********************");

            }

//			System.out.println("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            return false;
        }
    }
    //-----------------------------------------Regex to format links-----------------------------------------------------
    public static List<String> extractNewUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        return containedUrls;
    }

    public List<String> retrieveLinks() {
        return this.links;
    }
    //-----------------------------Retrieve url to go into the next page to scrape--------------------------------------
    public String getNextURL(String currentURL){
        String nextLink = "";
        try{
            Connection connection = Jsoup.connect(currentURL).userAgent(USER_AGENT);
            Document htmlDoc = connection.get();
            this.htmlDoc = htmlDoc;
            //--------------------------Find the url for next page------------------------------------------------------
            Elements links = htmlDoc.select("span.next-button>a[href]");
            for (Element n : links)//Foreach loop to get each element in linksOnPage
            {
                List nextPageLink = Crawler.extractNewUrls(n.toString());
                nextLink = (String) nextPageLink.get(0);
            }
        }
        catch (IOException ioe) {
        }
        return nextLink;
    }
}