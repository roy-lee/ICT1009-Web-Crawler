import java.sql.*;

public class DatabaseConnection {

    static Connection dbConnection = null;
    static PreparedStatement dbPrepareStat = null;

    public static void main(String[] args) throws Exception {
//        createSchema();
        makeJDBCConnection();
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

    public static void makeJDBCConnection() {

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
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/WebScrapper", "root", "");
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

    public static boolean eixistingTitleChecker(String titleName) throws SQLException {
        // MySQL Select Query Tutorial


        try {
            String getQueryStatement = "SELECT * FROM scrappedData where articleTitle = ?";
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
            if (!rs.isBeforeFirst() ) {
                return true;
            } else {
                return false;
            }
        }

    public static int getLastID() throws SQLException {
        int dataID = 0;
        // MySQL Select Query Tutorial
        String getQueryStatement = "SELECT * FROM scrappedData";

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
            dataID = rs.getInt("dataID");
        }
        return dataID;
    }

    public static void addDataToDB(int dataID, String articleTitle, String articleDate, String articleSource, String scrappedAt) {

        try {

            String insertQueryStatement = "INSERT  INTO  scrappedData  VALUES  (?,?,?,?,?)";
            dbPrepareStat = dbConnection.prepareStatement(insertQueryStatement);

            dbPrepareStat.setInt(1, dataID);
            dbPrepareStat.setString(2, articleTitle);
            dbPrepareStat.setString(3, articleDate);
            dbPrepareStat.setString(4, articleSource);
            dbPrepareStat.setString(5,scrappedAt);

            // execute insert SQL statement
            dbPrepareStat.executeUpdate();
            System.out.println("added successfully on " + scrappedAt);
            System.out.println("********************");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void getDataFromDB() {

        try {
            // MySQL Select Query Tutorial
            String getQueryStatement = "SELECT * FROM scrappedData";

            dbPrepareStat = dbConnection.prepareStatement(getQueryStatement);

            // Execute the Query, and get a java ResultSet
            ResultSet rs = dbPrepareStat.executeQuery();

            // Let's iterate through the java ResultSet
            while (rs.next()) {
                int dataID = rs.getInt("dataID");
                String articleTitle = rs.getString("articleTitle");
                String articleDate = rs.getString("articleDate");
                String articleSource = rs.getString("articleSource");
                String scrappedAt = rs.getString("scrappedAt");

                // Simply Print the results
                System.out.format("%s, %s, %s, %s, %s\n", dataID, articleTitle, articleDate, articleSource, scrappedAt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}


//}
