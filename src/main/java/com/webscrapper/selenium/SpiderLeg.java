package com.webscrapper.selenium;

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

//				ArrayList<String> redditDateTimeReplies = RedditHandler.grabRawRedditRepliesDateTimeHtml(redditUrl);
//				Element timestamp = htmlDocument.select("time").first();
//				String dateTime = timestamp.attr("datetime");
//				System.out.println("DateTime title: " + dateTime);
//
//                for (String redditDateTime : redditDateTimeReplies) {
//                	System.out.println(redditDateTime);
//				}

				Connection redditPostPage = Jsoup.connect(redditUrl).userAgent(USER_AGENT);
				Document element = redditPostPage.get();
				Elements pageTitle = element.select("a.title");

				redditReplies.remove(0);

				int numberOfComments= 0, tempPostID = 0;
				int positiveComment = 0,positiveSentimentValue= 0, posVal = 0;
				int negativeComment = 0,negativeSentimentValue = 0, negVal = 0;
				int neutralComment = 0,neutralSentimentValue = 0, neutralVal = 0;
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
								redditDatabaseConnection.addCommentToDB(commentID,redditReply, "00:00:00.000000", pageTitle.text().toString());
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
						//Here you determine second biggest, but you know that a is largest
					}

					if (negVal > posVal && negVal > neutralVal) {
						dataSentiment = "Negative";
						//Here you determine second biggest, but you know that b is largest
					}

					if (neutralVal > negVal && neutralVal > posVal || posVal == negVal) {
						dataSentiment = "Neutral";
						//Here you determine second biggest, but you know that c is largest
					}
					redditDatabaseConnection.addSentimentToDB(tempPostID, dataSentiment);
					tempPostID = postID;

				}





				// create method to check return true false in postsentiment ****
//				redditDatabaseConnection.addSentimentToDB(postID,dataSentiment);

				if (numberOfComments == 0) {
					System.out.println("No new comments since last scrape.");
					System.out.println("Overall sentiment: " + dataSentiment);
				} else {
					System.out.println("Scrapped " + numberOfComments + " comments.");
//					System.out.println(posVal + " positive points");
//					System.out.println(Math.abs(negVal) + " negative points");
//					System.out.println(Math.abs(neutralVal) + " neutral points");
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
//		System.out.println("Searching for the word " + searchWord + "...");
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