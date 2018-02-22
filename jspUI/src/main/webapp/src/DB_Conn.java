/*JDBC Connection From Database (MySql to Java)
 
 * Import libary -> import java.sql.*;
 * Download device driver(mysql-connector) (Extract the file)
 * Register the driver (Project(Right Click)>Build Path>Configure Build PAth (Click on libraries)>Add JARs(Select Jar file from above))
 * Load the database driver, make sure throws exception on method  -> Class.forName("com.mysql.jdbc.Driver");
 * Create Connection to database : Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","password"); //test = database name
 * Create Retrieving From DataBase Query: String query = "SELECT * FROM TABLENAME";
 * 										  Statement st = con.createStatement();
										  ResultSet rs =st.executeQuery(query);
 * Retrieving Data : String userData = " ";
					 while(rs.next()) //rs.next() will automatically get data until there is no more row in the database 
					{
						userData = rs.getInt(1)+ " : " + rs.getString(2) +  " : " + rs.getString(3);
						System.out.println(userData);
					} 
 *Close Connection: st.close();
					con.close();
*/

/*					Database name:test
					Table name : fakedata
+-------------+--------------+------+-----+---------+-------+
| Field       | Type         | Null | Key | Default | Extra |
+-------------+--------------+------+-----+---------+-------+
| Id          | varchar(9)   | YES  |     | NULL    |       |
| Name        | varchar(120) | YES  |     | NULL    |       |
| DESCRIPTION | longtext     | YES  |     | NULL    |       |
+-------------+--------------+------+-----+---------+-------+
+------+-------+-------------------+
| Id   | Name  | DESCRIPTION       |
+------+-------+-------------------+
| 1    | JAMES | JAMES IS A PERSON |
| 2    | Bob   | Bob IS A PERSON   |
+------+-------+-------------------+
*/

package Project;

import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

class DataBaseObj
{
	public List getRedditPosts(PrintWriter out)  //method to retrieve from redditpost table
	{
		try 
        {	
            // Load the database driver, make sure throws exception on method
			Class.forName("com.mysql.jdbc.Driver");
            //Create Connection to database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/redditdata","root","");
            
            
			//Create Query
			String query = "SELECT * FROM redditpost";
			
			//Process Query
			Statement st = con.createStatement();
			ResultSet rs =st.executeQuery(query);
            List<RedditPost> rlist = new ArrayList<RedditPost>();
            while(rs.next()) { //Store each row of data into the object and add that object into a list to return
                RedditPost robj = new RedditPost(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4));
                rlist.add(robj);
            }
            
            st.close();
            con.close();
            
			return rlist;
		}
		catch(Exception ex) 
		{
			out.println(ex);
		}
        return null;
	}
    
    public List getRedditComments(PrintWriter out, String pid) { //method to retrieve from redditcomment table
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Create Connection to database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/redditdata","root","");
            
            
			//Create Query
			String query = "SELECT * FROM redditcomment  where redditPost_PostID="+pid+";";
			
			//Process Query
			Statement st = con.createStatement();
			ResultSet rs =st.executeQuery(query);
            List<RedditComment> rlist = new ArrayList<RedditComment>();
            while(rs.next()) { //Store each row of data into the object and add that object into a list to return
                RedditComment robj = new RedditComment(rs.getInt(1),rs.getString(3),rs.getString(4));
                rlist.add(robj);
            }
            
            st.close();
            con.close();
            
			return rlist;
		}
		catch(Exception ex) 
		{
			out.println(ex);
		}
        return null;
    }

    public RedditPost getRedditPostByID(PrintWriter out, String pid) { //method to retrieve from redditcomment table
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Create Connection to database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/redditdata","root","");
            
            
            //Create Query
            String query = "SELECT * FROM redditpost where PostID="+pid+";";
            
            //Process Query
            Statement st = con.createStatement();
            ResultSet rs =st.executeQuery(query);
            rs.next();
            RedditPost robj = new RedditPost(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4));
            
            st.close();
            con.close();
            
            return robj;
        }
        catch(Exception ex) 
        {
            out.println(ex);
        }
        return null;
    }
}

class RedditPost { //Class object to store information retrieved from redditpost table
    int sn;
    String title, link, senti;
    public RedditPost(int sn, String title, String link, String senti) {
        this.sn = sn;
        this.title = title;
        this.link = link;
        this.senti = senti;
    }
}

class RedditComment { //Class object to store information retrieved from redditcomment table
    int sn;
    String comment, senti;
    public RedditComment(int sn, String comment, String senti) {
        this.sn = sn;
        this.comment = comment;
        this.senti = senti;
    }
}
