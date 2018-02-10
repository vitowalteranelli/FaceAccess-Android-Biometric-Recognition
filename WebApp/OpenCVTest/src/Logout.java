import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
				 maxFileSize=1024*1024*50,          // 50 MB
				 maxRequestSize=1024*1024*100)      // 100 MB
public class Logout extends HttpServlet {
	
	
	private Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();


    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    	throws IOException {

    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	
    	 out.write("<html><body>");

	}
        
        
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        	throws ServletException, IOException {

        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	boolean access = false;
        	out.write("<html><body>");
            	
            	String userstring = request.getParameter("username");
                
                //TODO
                /// /// /// session check
                Opencv.removeUser(userstring);
                /*
                HttpSession s = sessions.get(userstring);
                if (s!=null)
                {
                	System.out.println("utente in fase di rimozione: "+ userstring); 
                    
                    sessions.remove(userstring);
                    System.out.println("utente sloggato");
                	s.invalidate();
                	
                }else {
                	System.out.println("<p>username precedentemente non loggato</p>");
                	
                
                }
                */
                
            out.write("</body></html>");
        	
        	
        	
    	}
        
}