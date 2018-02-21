package com.webscrapper.selenium;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider
{
      private static final int MAX_PAGES_TO_SEARCH = 5;
      private Set<String> pagesVisited = new HashSet<String>();
      private List<String> pagesToVisit = new LinkedList<String>();
      int currentPagesScraped = 0;

      /**
       * Our main launching point for the Spider's functionality. Internally it creates spider legs
       * that make an HTTP request and parse the response (the web page).
       *
       * @param url
       *            - The starting point of the spider
       * @param searchWord
       *            - The word or string that you are searching for
       */
      public void search(String url, String searchWord)
      {
          //----------------------------If pages searched is within search quota----------------------------
          String currentUrl;

          SpiderLeg leg = new SpiderLeg();
          //Add url if list is empty
          if(this.pagesToVisit.isEmpty()) {
              currentUrl = url;
              this.pagesVisited.add(url);
          }
          //If not, go to the next url
          else {
              currentUrl = this.nextUrl();
          }
          while (currentPagesScraped < MAX_PAGES_TO_SEARCH){
              leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in SpiderLeg
              String nextLink = leg.retrieveURL(currentUrl);
              currentUrl = nextLink;
              //System.out.println("Next link: " + nextLink);
              System.out.println("---------------------------------------------------------------To next page---------------------------------------------------------");
              currentPagesScraped++;
          }
          boolean success = leg.searchForWord(searchWord);
          if(success) {
//              System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
//              System.out.println(currentUrl);
              //break;
          }
          this.pagesToVisit.addAll(leg.getLinks());
      }
      /**
       * Returns the next URL to visit (in the order that they were found). We also do a check to make
       * sure this method doesn't return a URL that has already been visited.
       *
       * @return
       */
      private String nextUrl()
      {
          String nextUrl;
          do
          {
              nextUrl = this.pagesToVisit.remove(0);

          } while(this.pagesVisited.contains(nextUrl));
          this.pagesVisited.add(nextUrl);
          System.out.println("next url is: " + nextUrl);
          return nextUrl;
      }
}

