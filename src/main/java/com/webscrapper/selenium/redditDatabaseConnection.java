package com.webscrapper.selenium;
import java.sql.*;

public class redditDatabaseConnection {

    static Connection dbConnection = null;
    static PreparedStatement dbPrepareStat = null;

    public static void main(String[] args) {
//        createSchema();
        makeRedditConnection();
        getDataFromDB();
//        new java.sql.Timestamp(new java.util.Date().getTime());
//        addDataToDB(100,"Facebook scrapped","00:00:00.000000","Facebook TODAY", "00:00:00.000000");
        System.out.println("After adding...");
        getDataFromDB();
//        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//        server.setHandler(context);
//        context.addServlet(new ServletHolder(new Write()), "/write");
//        context.addServlet(new ServletHolder(new Read()), "/read");
//        server.start();
//        server.join();
    }

//    private static void createSchema() throws SQLException {
//        Connection connection = getConnection();
//        Statement stmt = connection.createStatement();
//        stmt.executeUpdate("DROP TABLE IF EXISTS scrappedData");
//        stmt.executeUpdate("CREATE TABLE scrappedData (dataID int NOT NULL AUTO_INCREMENT, articleTitle varchar(255), articleDate TIMESTAMP(6), articleSource varchar(50), scrappedAt TIMESTAMP(6), PRIMARY KEY (dataID))");
//        connection.close();
//    }

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
        // MySQL Select Query Tutorial

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
        // MySQL Select Query Tutorial

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
        // Let's iterate through the java ResultSet
        return !rs.isBeforeFirst();
    }

    public static int getLastID() throws SQLException {
        int dataID = 0;
        // MySQL Select Query Tutorial
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
        // MySQL Select Query Tutorial
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

    public static int getPostID(String redditPostTitle) throws SQLException {
        int postID = 0;
        // MySQL Select Query Tutorial
        String getQueryStatement = "SELECT * FROM redditPost WHERE postTitle = ?";

        try {
            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);
            dbPrepareStat.setString(1, redditPostTitle);
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
        return postID;
    }


    public static void addCommentToDB(int commentID, String commentData, String commentDate, String postTitle) throws SQLException {

        int postID = 0;
        // MySQL Select Query Tutorial
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
            dbPrepareStat.setString(4, commentDate);

            System.out.println(dbPrepareStat.toString());

            // execute insert SQL statement
            dbPrepareStat.executeUpdate();

            System.out.println("Comment is " + commentData + "\nCommentID is " +commentID);
            System.out.println("Post title is " + postTitle + "\nPostID is " + getPostID(postTitle));
            System.out.println("CommentDate is " + commentDate);

            System.out.println("added successfully on " + commentDate);
            System.out.println("********************");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void addDataToDB(int postID, String postTitle, String postLink, String postDate) {

        try {

            String insertQueryStatement = "INSERT  INTO  redditPost  VALUES  (?,?,?,?)";
            dbPrepareStat = dbConnection.prepareStatement(insertQueryStatement);

            dbPrepareStat.setInt(1, postID);
            dbPrepareStat.setString(2, postTitle);
            dbPrepareStat.setString(3, postLink);
            dbPrepareStat.setString(4, postDate);

            // execute insert SQL statement
            dbPrepareStat.executeUpdate();
            System.out.println("added successfully on " + postDate);
            System.out.println("********************");
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

            // Let's iterate through the java ResultSet
            while (rs.next()) {
                int postID = rs.getInt("postID");
                String postTitle = rs.getString("postTitle");
                String postLink = rs.getString("postLink");
                String postDate = rs.getString("postDate");

                // Simply Print the results
                System.out.format("%s, %s, %s, %s, %s\n", postID, postTitle, postLink, postDate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}


//}
