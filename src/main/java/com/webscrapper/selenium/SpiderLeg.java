package com.webscrapper.selenium;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpiderLeg{
	// Use a fake USER_AGENT so the web server thinks the robot is a normal web browser
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
    private static final int MAX_THREADS_PER_PAGE = 25;
    int numOfThreads = 0;

	/**
	 * This performs all the work. It makes an HTTP request, checks the response,
	 * and then gathers up all the links on the page. Perform a searchForWord after
	 * the successful crawl
	 * 
	 * @param url
	 *            - The URL to visit
	 * @return whether or not the crawl was successful
	 */
	public boolean crawl (String url) {
		try {
			//----------------Established a connection to the web page, return false if failed--------------------------
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;

			//--------------------Connection to the page has been established successfully------------------------------
			if (connection.response().statusCode() == 200){ // 200 is the HTTP OK status code
				System.out.println("\n**Visiting** Received web page at " + url);
			}
			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("**Failure** Retrieved something other than HTML");
				return false;
			}

            //-----------------Filters out pages not in the scope-------------------------------------------------------
            Elements linksOnPage = htmlDocument.select("a[href]");
			String userInput = "https://www.reddit.com/r/singapore/comments/";//This is the filtering
			String firstformatlink = "";

			for (int i = 0; i < 1; i++) {	
				for (Element e : linksOnPage)   {//Foreach loop to get each element in linksOnPage
			        if (e.tagName("href").toString().contains(userInput)) {
			            String filteredLinks = e.toString();
			            firstformatlink = firstformatlink + filteredLinks;
			        }
				}
			}
			List FormattedUrls = SpiderLeg.extractUrls(firstformatlink);
			//-----------------------------A list of url found----------------------------------------------------------
			//System.out.println("Formatted url: "+ FormattedUrls);
			//---------------------Remove excessive threads(Ex. pinned threads) before scraping----------------------------
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
            for (int i = 0; i < MAX_THREADS_PER_PAGE; i++) {
                String redditUrl = (String) FormattedUrls.get(i);

                RedditHandler redditHandler = new RedditHandler();
                ArrayList<String> redditReplies = RedditHandler.grabRawRedditRepliesHtml(redditUrl);

                System.out.println("-----------------------------New post------------------------------------");
                System.out.println(String.format("[POST] %s", redditUrl));

				Connection redditPostPage = Jsoup.connect(redditUrl).userAgent(USER_AGENT);
				Document element = redditPostPage.get();
				Elements pageTitle = element.select("a.title");
				System.out.println("Page title: " + pageTitle.text());

                redditReplies.remove(0);

				try {
					if (redditDatabaseConnection.existingTitleChecker(pageTitle.text()) != false) {
						redditDatabaseConnection.addDataToDB(postID,pageTitle.text().toString(),redditUrl,"00:00:00.000000");
						postID += 1;
					} else {
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

                for(String redditReply : redditReplies)
                {
//                	redditDatabaseConnection.addCommentToDB(commentID,redditReply,"00:00:00.000000",pageTitle.text().toString());
//					commentID += 1;
//					redditDatabaseConnection.addCommentToDB(commentID,redditReply, "00:00:00.000000", pageTitle.text().toString());
					try {
						if (redditDatabaseConnection.existingCommentChecker(redditReply) != false) {
							redditDatabaseConnection.addCommentToDB(commentID,redditReply, "00:00:00.000000", pageTitle.text().toString());
//							System.out.println(String.format("[COMMENT] %s", redditReply));
							commentID += 1;
						} else {
							break;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

                }
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
	public static List<String> extractUrls(String text)
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
	/**
	 * Performs a search on the body of on the HTML document that is retrieved. This
	 * method should only be called after a successful crawl.
	 * 
	 * @param searchWord
	 *            - The word or string to look for
	 * @return whether or not the word was found
	 */
	public boolean searchForWord(String searchWord) {
		// Defensive coding. This method should only be used after a successful crawl.
		if (this.htmlDocument == null) {
			System.out.println("ERROR! Call crawl() before performing analysis on the document");
			return false;
		}
		System.out.println("Searching for the word " + searchWord + "...");
		String bodyText = this.htmlDocument.body().text();
		return bodyText.toLowerCase().contains(searchWord.toLowerCase());
	}

	public List<String> getLinks() {
		return this.links;
	}
	public String retrieveURL(String currentURL){
        String nextLink = "";
        try{
            Connection connection = Jsoup.connect(currentURL).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            //--------------------------Find the url for next page------------------------------------------------------
            Elements links = htmlDocument.select("span.next-button>a[href]");
            for (Element n : links)//Foreach loop to get each element in linksOnPage
            {
                List nextPageLink = SpiderLeg.extractUrls(n.toString());
                nextLink = (String) nextPageLink.get(0);
                //System.out.println(String.format("Next link: -----------------------------------------" + nextLink));
            }
        }
        catch (IOException ioe) {
        }
        return nextLink;
    }
}