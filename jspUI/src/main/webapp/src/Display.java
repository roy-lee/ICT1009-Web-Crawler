package Project;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class Display extends HttpServlet {
    PrintWriter out;
    Display_Share ds = new Display_Share(); //initialise shared class between displays
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        out = response.getWriter(); //object to write html codes

        DataBaseObj obj1 = new DataBaseObj();  //create new database object
        List rlist =obj1.getRedditPosts(out); //get all posts from database

        ds.printHeadTag("Display",out); //print head tag information

        out.println("<BODY>");

        out.println("<h1>Displaying data from database</h1>");
        displayList(rlist); //method to display table
        out.println("<h1>End of database</h1>");


        out.println("</BODY>");
        out.println("</HTML>");

    }
    public void displayList(List rlist) { //method to display table
        out.println("<table class='sortable'>"); //class for javascript to sort table
        out.println("<tr>");
        out.println("<th>S/N</th>");
        out.println("<th>Title</th>");
        out.println("<th>Link</th>");
        out.println("<th>Sentiment</th>");
        out.println("<th>Comments</th>");
        out.println("</tr>");
        for (int i=0;i<rlist.size();i++) {
            RedditPost robj = (RedditPost) rlist.get(i); //get the object and its attributes
            out.println("<tr>");
            out.println("<td>"+robj.sn+"</td>");
            out.println("<td>"+robj.title+"</td>");
            out.println("<td><a target='_blank' href="+robj.link+">"+robj.link+"</a></td>");
            out.println("<td>"+ds.decideEmoticon(robj.senti)+"</td>");
            out.println("<td><a href='./DisplayComments?pid="+robj.sn+"'>View comments</a></td>");
            out.println("</tr>");
     }
     out.println("</table>");
    }
}

class Display_Share { //class to store some shared methods between display classes
    public String decideEmoticon(String senti) { //method to set image path of emoticons
        String path = "<figure><img src='./Images/"+senti+".jpg' height='32' width='32'><figcaption>"+senti+"</figcaption></img>";
        return path;
    }
    public void printHeadTag(String title, PrintWriter out){
        out.println("<!DOCTYPE html>");
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<TITLE>"+title+"</TITLE>");
        out.println("<link rel='stylesheet' href='./css/custom.css'></link>"); //css
        out.println("<script src='./js/sorttable.js'></script>"); //javascript
        out.println("</HEAD>");
    }
}