package com.webscrapper.selenium;

public class SiteInfo {

    static String siteName;
    static String dataTitle;
    static String dataTime;

    SiteInfo(String newSiteName, String newDataTitle, String newDataTime) {
        siteName = newSiteName;
        dataTitle = newDataTitle;
        dataTime = newDataTime;
    }


    public static String getSiteName() {
        return siteName;
    }

    static String getSiteDataTitle() {
        return dataTitle;
    }

    static String getSiteDataTime() {
        return dataTime;
    }

    public static String getSiteInfo() {
        return "Scrapping: " + SiteInfo.getSiteName() +
                "\nTitle CSS Selector: " + SiteInfo.getSiteDataTitle() +
                "\nDate CSS Selector: " + SiteInfo.getSiteDataTime();
    }

}
