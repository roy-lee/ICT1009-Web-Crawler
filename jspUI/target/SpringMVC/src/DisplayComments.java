package Project;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class DisplayComments extends HttpServlet {
    PrintWriter out;
    public Display_Share ds = new Display_Share(); //initialise shared class between displays

 public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     out = response.getWriter(); //object to write html codes
     
     DataBaseObj obj1 = new DataBaseObj(); //create new database obj
     
     String query = request.getQueryString();
     String[] parts = query.split("="); //additional info after url, split it by the equal sign, 2nd part will contain post id
     
     List rlist =obj1.getRedditComments(out,parts[1]); //collate list by post id from the database
     
     ds.printHeadTag("Display Comments",out); //print head tag information
     
     out.println("<BODY>");
     int[] pnn = getPNN(rlist); //get positive/neutral/negative (pnn) counts from list
     out.println("<img src='./PChart?p="+pnn[0]+"&n="+pnn[1]+"&n="+pnn[2]+"'></img>"); //call pie chart servlet with the pnn attributes
     
     out.println("<h1>Displaying data from database</h1>");
     displayList(rlist); //method to display list in table form
     out.println("<h1>End of database</h1>");
     
     out.println("<a href='./Display'>Back to main page</a>");
     
     out.println("</BODY>");
     out.println("</HTML>");
 }
    public void displayList(List rlist) { //method to display list in table form
        out.println("<table class='sortable'>"); //class for javascript to sort table
        out.println("<tr>");
        out.println("<th>S/N</th>");
        out.println("<th>Comment</th>");
        out.println("<th>Sentiment</th>");
        out.println("</tr>");
        for (int i=0;i<rlist.size();i++) {
            RedditComment robj = (RedditComment) rlist.get(i); //get the object and its attributes
            out.println("<tr>");
            out.println("<td>"+robj.sn+"</td>");
            out.println("<td>"+robj.comment+"</td>");
            out.println("<td>"+ds.decideEmoticon(robj.senti)+"</td>");      
            out.println("</tr>");
        }
        out.println("</table>");
    }
    
    public int[] getPNN(List rlist){ //method to count pnn
        int[] pnn = new int[3];
        for(int i=0; i<rlist.size(); i++) {
            RedditComment robj = (RedditComment) rlist.get(i);
            switch(robj.senti){
                case "positive":
                    pnn[0] += 1;
                    break;
                case "neutral":
                    pnn[1] += 1;
                    break;
                case "negative":
                    pnn[2] += 1;
                    break;
                default:
                    break;
            }
        }
        return pnn;
    }

}