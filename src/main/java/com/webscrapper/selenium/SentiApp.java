package com.webscrapper.selenium;

import uk.ac.wlv.sentistrength.*;

import java.lang.reflect.Type;

public class SentiApp {
    public static void main(String[] args) {
//Method 2: One initialisation and repeated classifications
        SentiStrength sentiStrength = new SentiStrength();
//Create an array of command line parameters to send (not text or file to process)
        String ssthInitialisation[] = {"sentidata", "/Users/roylee/Downloads/SentiStrength/SentiStrength_DataEnglishFeb2017/", "trinary"};
        sentiStrength.initialise(ssthInitialisation); //Initialise
        //Trinary mode: -1 = negative, 0 = neutral, 1 = positive
//can now calculate sentiment scores quickly without having to initialise again
//        System.out.println(sentiStrength.computeSentimentScores("I love dogs."));


        // trinary
        String emotion = sentiStrength.computeSentimentScores("I hate dogs.");
        String[] array = emotion.split(" ", -1);
        for (String senti : array) {
            Integer.valueOf(senti);
        }



        // explain
        String value = emotion.substring(emotion.length()-6, emotion.length()-5);
        if (value.equals(">")) {
            System.out.println("Positive");
        } else if (value.equals("<")) {
            System.out.println("Negative");
        }



    }
}
