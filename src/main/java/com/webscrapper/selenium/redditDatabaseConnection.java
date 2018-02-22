package com.webscrapper.selenium;
import com.mysql.jdbc.MysqlDataTruncation;

import java.sql.*;

public class redditDatabaseConnection {

    static Connection dbConnection = null;
    static PreparedStatement dbPrepareStat = null;

    public static void main(String[] args) {
//        createSchema();
        makeRedditConnection();
        getDataFromDB();
        System.out.println("After adding...");
        getDataFromDB();
    }

    public static void makeRedditConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Congrats - Seems your MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
            e.printStackTrace();
            return;
        }

        try {
            // DriverManager: The basic service for managing a set of JDBC drivers.
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/redditData", "root", "");
            if (dbConnection != null) {
                System.out.println("Connection Successful!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
            return;
        }

    }

    public static boolean existingCommentChecker(String commentBody) throws SQLException {
        try {
            String getQueryStatement = "SELECT * FROM redditComment where commentData = ?";
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);

            dbPrepareStat.setString(1, commentBody);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Execute the Query, and get a java ResultSet
        ResultSet rs = null;
        try {
            rs = dbPrepareStat.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Let's iterate through the java ResultSet
        return !rs.isBeforeFirst();
    }


    public static boolean existingTitleChecker(String titleName) throws SQLException {
        try {
            String getQueryStatement = "SELECT * FROM redditPost where postTitle = ?";
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);

            dbPrepareStat.setString(1, titleName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Execute the Query, and get a java ResultSet
        ResultSet rs = null;
        try {
            rs = dbPrepareStat.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Iterate through the java ResultSet
        return !rs.isBeforeFirst();
    }

    public static int getLastID() throws SQLException {
        int dataID = 0;
        String getQueryStatement = "SELECT * FROM redditPost";

        try {
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Execute the Query, and get a java ResultSet
        ResultSet rs = null;
        try {
            rs = dbPrepareStat.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (rs.next()) {
            dataID = rs.getInt("postID");
        }
        return dataID;
    }

    public static int getLastCommentID() throws SQLException {
        int commentID = 0;
        String getQueryStatement = "SELECT * FROM redditComment";

        try {
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Execute the Query, and get a java ResultSet
        ResultSet rs = null;
        try {
            rs = dbPrepareStat.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (rs.next()) {
            commentID = rs.getInt("commentID");
        }
        return commentID;
    }

    public static void addCommentToDB(int commentID, String commentData, String postTitle, String commentSentiment) throws SQLException {
        int postID = 0;
        String getQueryStatement = "SELECT * FROM redditPost WHERE postTitle = ?";

        try {
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);
            dbPrepareStat.setString(1, postTitle);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Execute the Query, and get a java ResultSet
        ResultSet rs = null;
        try {
            rs = dbPrepareStat.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (rs.next()) {
            postID = rs.getInt("postID");
        }


        try {

            String insertQueryStatement = "INSERT  INTO  redditComment  VALUES  (?,?,?,?)";
            dbPrepareStat = dbConnection.prepareStatement(insertQueryStatement);

            dbPrepareStat.setInt(1, commentID);
            dbPrepareStat.setInt(2, postID);
            dbPrepareStat.setString(3, commentData);
            dbPrepareStat.setString(4, commentSentiment);

//            System.out.println(dbPrepareStat.toString());

            // execute insert SQL statement
            dbPrepareStat.executeUpdate();

//            System.out.println("Comment is " + commentData + "\nCommentID is " +commentID);
//            System.out.println("Post title is " + postTitle + "\nPostID is " + getPostID(postTitle));
//            System.out.println("CommentDate is " + commentDate);
//
//            System.out.println("added successfully.");
//            System.out.println("********************");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void addDataToDB(int postID, String postTitle, String postLink, String sentimentPlaceHolder) {
        try {

            String insertQueryStatement = "INSERT  INTO  redditPost  VALUES  (?,?,?,?)";
            dbPrepareStat = dbConnection.prepareStatement(insertQueryStatement);

            dbPrepareStat.setInt(1, postID);
            dbPrepareStat.setString(2, postTitle);
            dbPrepareStat.setString(3, postLink);
            dbPrepareStat.setString(4, sentimentPlaceHolder);

            // execute insert SQL statement
            dbPrepareStat.executeUpdate();
            System.out.println("Post added successfully.");
//            System.out.println("********************");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSentimentToDB(int postID, String sentiment) {
        try {

            String insertQueryStatement = "UPDATE redditPost SET postSentiment = ? WHERE postID = ?";
            dbPrepareStat = dbConnection.prepareStatement(insertQueryStatement);

            dbPrepareStat.setString(1, sentiment);
            dbPrepareStat.setInt(2, postID);

//            System.out.println(dbPrepareStat.toString());
            // execute insert SQL statement
            dbPrepareStat.executeUpdate();
//            System.out.println("Post has " + sentiment + " sentiment");
//            System.out.println("********************");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void getDataFromDB() {
        try {
            // MySQL Select Query Tutorial
            String getQueryStatement = "SELECT * FROM redditPost";

            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);

            // Execute the Query, and get a java ResultSet
            ResultSet rs = dbPrepareStat.executeQuery();

            // Iterate through the java ResultSet
            while (rs.next()) {
                int postID = rs.getInt("postID");
                String postTitle = rs.getString("postTitle");
                String postLink = rs.getString("postLink");
                String postDate = rs.getString("postDate");

                // Print the results
                System.out.format("%s, %s, %s\n", postID, postTitle, postLink);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
