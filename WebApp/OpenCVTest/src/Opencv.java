import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.IntBuffer;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;


@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
				 maxFileSize=1024*1024*50,          // 50 MB
				 maxRequestSize=1024*1024*100)      // 100 MB
public class Opencv extends HttpServlet {
	
	
	private static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	
		EncryptionUtil encObject;
		
	    String roottot = "/home/cheggynho/workspace/OpenCVTest";
	    String nomeapp = "OpenCVTest";
	    String webapps = "/webapps/";
	    String rootwebapp = System.getProperty("catalina.base")+webapps+nomeapp;

	    String rootworking_trash = "/home/cheggynho/working_trash/";
	    String desk = "desk/";
	    static{ System.load("/home/cheggynho/opencv_built/lib/libopencv_java249.so"); }


    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    	throws IOException {

    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	
    	 out.write("<html><body>");
    	 out.write("pagliaRa");
	}
        
        
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        	throws ServletException, IOException {
    		
    		System.out.println("Server> Comunicazione in ingresso...");
        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	boolean access = false;
        	out.write("Risposta: ");
        	String firstname = "";
        	String lastname = "";
        	String user_ret = "";
        	String pass_ret = "";
        	String first_ret = "";
        	String last_ret = "";
        	String mail_ret = "";
        	String imei_ret = "";
        	
        	
            
            if(ServletFileUpload.isMultipartContent(request)){
            	
            	String userstring = request.getHeader("username");
                String uploadFilePath = rootworking_trash;
                  
                // creates the save directory if it does not exists
                File fileSaveDir = new File(uploadFilePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                    out.write("<p>CHECK if</p>");
                }
                
                HttpSession s = sessions.get(userstring);
                if (s!=null)
                {
                String last_access = s.getAttribute(userstring).toString();
                out.write("Ciao "+s.getAttribute("name").toString()+", sei loggato! ");
                out.write("ultimo accesso : "+last_access+" ");
                System.out.println("multipartInfo> UserName loggato!");
                }else {
                	System.out.println("multipartInfo> Username precedentemente non loggato");
                	
                
                String fileName = null;
                //Get all the parts from request and write it to the file on server
                for (Part part : request.getParts()) {
                    fileName = getFileName(part);
                    part.write(uploadFilePath + fileName);
                }
                
                byte[] public_key = null;
                InputStream is = request.getPart("public_client").getInputStream();
                String public_string = IOUtils.toString(is, "UTF-8");
                public_key = Base64.decodeBase64(public_string);
                System.out.println("multipartInfo> estratta chiave pubblica client dallo stream");
                
                
                byte[] pass_byte = null;
                is = request.getPart("pass_byte").getInputStream();
                String myString = IOUtils.toString(is, "UTF-8");
                System.out.println("multipartInfo> estratta password criptata dallo stream");
                byte[] pass_byte2 = Base64.decodeBase64(myString);
                String prova_pass2 =new String(pass_byte2);
                              
                is = request.getPart("imei").getInputStream();
                String imei_64 = IOUtils.toString(is, "UTF-8");
                System.out.println("multipartInfo> estratto imei dallo stream");
                byte[] imei_byte = Base64.decodeBase64(imei_64);
                
                byte[] public_client;
                String imei;
                
                try {
                	
                	byte[] privateKeyBytes;
                	KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	PublicKey public_ = kf.generatePublic(new X509EncodedKeySpec(public_key));
                	//byte[] cipherText = encObject.encrypt("AAA", public_);
                	
                	ObjectInputStream inputStream = null;
                	
                    // Encrypt the string using the public key
                    inputStream = new ObjectInputStream(new FileInputStream(encObject.PUBLIC_KEY_FILE));
                    PublicKey publicKey = (PublicKey) inputStream.readObject();
                    //String prova = "prova";
                    //byte[] cipherProva = encObject.encrypt(prova, publicKey);
                    
                    inputStream = new ObjectInputStream(new FileInputStream(encObject.PRIVATE_KEY_FILE));
                    PrivateKey privateKey = (PrivateKey) inputStream.readObject();
                    String password_screen = encObject.decrypt(pass_byte2, privateKey);
                    String imei_screen = encObject.decrypt(imei_byte, privateKey);
                
                    public_client = public_.getEncoded();
                    imei = imei_screen;
		        
                
                //db job
                
                // This is needed to use Connector/J. It basically creates a new instance
                // of the Connector/J jdbc driver.
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                System.out.println(" ");
                System.out.println("mysqlConnector> instanziato l'oggetto JDBC");
                // Open new connection.
                java.sql.Connection conn;

                System.out.println("mysqlConnector> creo la connessione al DB");
                conn = DriverManager.getConnection("jdbc:mysql://localhost/FaceAccessDB?user=root");
                
                
                
                // Generate the SQL query.
                PreparedStatement sqlStatement = conn.prepareStatement("SELECT public_client FROM devices WHERE application_code=? ;");
                sqlStatement.setString(1, imei);
                // Get the query results and display them.
                ResultSet sqlResult = sqlStatement.executeQuery();
                
                int counter_resultset = 0 ;
                
                KeyPairGenerator keyGen2 = KeyPairGenerator.getInstance("RSA");
                keyGen2.initialize(2048);
                PublicKey public_client_db = keyGen2.genKeyPair().getPublic();
                
                while(sqlResult.next()) {

                	counter_resultset++;
                	byte[] publicKeyBytes = sqlResult.getBytes("public_client");
                	kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	public_client_db = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                	System.out.println("mysqlConnector> estratta dal db la chiave pubblica del client");

                    System.out.println(" ");
             	   
                }
                
                if (counter_resultset==0){
                	
                	System.out.println("mysqlConnector> l'applicazione installata non è registrata nel DB");
                	// Generate the SQL query.
                	sqlStatement = conn.prepareStatement("INSERT INTO devices (application_code,public_client) VALUES (? , ?);");
                		
                	sqlStatement.setString(1, imei);
                	sqlStatement.setBytes(2, public_client);
                	// Get the query results and display them.
                	sqlStatement.execute();
                	System.out.println("mysqlConnector> Inserita tupla nel DB");

                    System.out.println(" ");
                	
                }
                
                
                ///////////////////////////////////////////////////
                
                
                /*
                
                
                String trainingDir = rootworking_trash+"/recog/";
                File root = new File(trainingDir);

                FilenameFilter imgFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        name = name.toLowerCase();
                        return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
                    }
                };

                File[] imageFiles = root.listFiles(imgFilter);
                //out.write("<p>Array di file caricato</p>");
                MatVector images = new MatVector(imageFiles.length);

                Mat labels = new Mat(imageFiles.length, 1, org.bytedeco.javacpp.opencv_core.CV_32SC1);
                IntBuffer labelsBuf = labels.getIntBuffer();
                //out.write("<p>Vettori istanziati</p>");
                int counter = 0;

                for (File image : imageFiles) {
                    Mat img = org.bytedeco.javacpp.opencv_highgui.imread(image.getAbsolutePath(), org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
                    //out.write("<p>"+(counter+1)+" immagine caricata: "+img.cols()+" colonne, "+img.rows()+" righe ;");
                    int label = Integer.parseInt(image.getName().split("\\-")[0]);
                    //String nome = image.getName().split("\\-")[1].split("_")[0];
                    //out.write(""  +(counter+1)+" etichetta caricata: "+nome+"</p>");
                    images.put(counter, img);

                    labelsBuf.put(counter, label);

                    counter++;
                }
               */ 
                
                //FaceRecognizer faceRecognizer = org.bytedeco.javacpp.opencv_contrib.createFisherFaceRecognizer();
                 FaceRecognizer faceRecognizer = org.bytedeco.javacpp.opencv_contrib.createEigenFaceRecognizer();
                // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()
                //out.write("<p>Recognizer caricato</p>");
                
                /*
                faceRecognizer.train(images, labels);
                //out.write("<p>dati addestrati</p>");
                */
                 faceRecognizer.load(rootworking_trash+"/recog/"+"eigenfaces_at.yml");

                 System.out.println("multipartInfo> Caricato il riconoscitore facciale addestrato");
                
                Mat test_ = org.bytedeco.javacpp.opencv_highgui.imread(rootworking_trash+"/prova.png", org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
                Mat img3= new Mat();
                org.bytedeco.javacpp.opencv_imgproc.resize(test_, img3, new Size(340,424));
                org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img3, img3);
                int predictedLabel = faceRecognizer.predict(img3);
                
                test_ = org.bytedeco.javacpp.opencv_highgui.imread(rootworking_trash+"/prova2.png", org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
                Mat img4= new Mat();
                org.bytedeco.javacpp.opencv_imgproc.resize(test_, img4, new Size(340,424));
                org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img4, img4);
                int predictedLabel2 = faceRecognizer.predict(img4);
                
                test_ = org.bytedeco.javacpp.opencv_highgui.imread(rootworking_trash+"/prova3.png", org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
                Mat img5= new Mat();
                org.bytedeco.javacpp.opencv_imgproc.resize(test_, img5, new Size(340,424));
                org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img5, img5);
                int predictedLabel3 = faceRecognizer.predict(img5);
                

                System.out.println("multipartInfo> Caricate immagini del client e predetti gli utenti");
                
                int predictedLabelTOT = 0;
                
                if ((predictedLabel==predictedLabel2)||(predictedLabel3==predictedLabel)){predictedLabelTOT=predictedLabel;}
                else if(predictedLabel3==predictedLabel2){predictedLabelTOT=predictedLabel2;}
                
                
                
                
                System.out.println("multipartInfo> Selezionato volto di cui verificare le credenziali con etichetta: " + predictedLabelTOT);

                
              ////out.write("<h2>Sei l'utente numero "+predictedLabelTOT+"</h2>");
                
                
                
                // Generate the SQL query.
                sqlStatement = conn.prepareStatement("SELECT * FROM users WHERE label=? ;");
                sqlStatement.setInt(1, predictedLabelTOT);
                // Get the query results and display them.
                sqlResult = sqlStatement.executeQuery();
                
                counter_resultset = 0 ;
                
                if (predictedLabelTOT!=0){
                
                while(sqlResult.next()) {

                	counter_resultset++;
                	
                	System.out.println("multipartInfo> Etichetta presente nel database");
                	
                	
                	byte[] password_db = sqlResult.getBytes("public");
                	String password_db_dec = encObject.decrypt(password_db, privateKey);
                	if (userstring.equals(sqlResult.getString("username"))&&(password_screen.equals(password_db_dec))){
                		
                			
                			access = true;
                			out.write("<h2>Ciao "+sqlResult.getString("firstname")+" "+sqlResult.getString("lastname")+"</h2>");
                			firstname = sqlResult.getString("firstname");
                			lastname = sqlResult.getString("lastname");

                			System.out.println("multipartInfo> Credenziali valide per l'utente: "+firstname+" "+lastname );
                			
                			user_ret = sqlResult.getString("username");
                			pass_ret = password_db_dec;
                			first_ret = sqlResult.getString("firstname");
                			last_ret = sqlResult.getString("lastname");
                			mail_ret = sqlResult.getString("mail");
                			imei_ret = imei;

                			out.write(";content=");
                			 byte[] ret_byte = EncryptionUtil.encrypt(user_ret, public_client_db);
                			 String string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			ret_byte = EncryptionUtil.encrypt(pass_ret, public_client_db);
                			string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			ret_byte = EncryptionUtil.encrypt(first_ret, public_client_db);
                			string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			ret_byte = EncryptionUtil.encrypt(last_ret, public_client_db);
                			string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			ret_byte = EncryptionUtil.encrypt(mail_ret, public_client_db);
                			string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			ret_byte = EncryptionUtil.encrypt(imei_ret, public_client_db);
                			string_ret = Base64.encodeBase64String(ret_byte);
                			out.write(string_ret);
                			out.write(";content=");
                			out.write("connection_validate");
                			
                			System.out.println("multipartInfo> i campi sono stati criptati e la risposta è pronta");
                			
                    	
                	}else {
                		
                		out.write("Credenziali non valide");
                		System.out.println("multipartInfo> Dati non corrispondono: Utente no "+predictedLabelTOT+" = Volto di "+sqlResult.getString("firstname")+" "+sqlResult.getString("lastname")+" "
                				+ "-- Username impostato: "+userstring+" "
                						+ "-- Password confrontate: "+password_screen+" // "+password_db_dec+" ");
                	}
                	
                	
                }
                
                
                
                
                
                if (counter_resultset==0){
                	
                	System.out.println("multipartInfo> Etichetta non presente nel database, procedere alla registrazione");
                	out.write("<h2>Etichetta non presente nel database, procedere alla registrazione</h2>");
                	
                	
                }
                
                }//end predictedlabel0 
                else{
                	
                	out.write("Riacquisire il volto");
                }
                
                
                //System.out.println("Contrasto 1 immagine: " + Math.round(contrast_measure(img3)));
                //System.out.println("Contrasto 2 immagine: " + Math.round(contrast_measure(img4)));
                //System.out.println("Contrasto 3 immagine: " + Math.round(contrast_measure(img5)));
                
                ////////////////////////////////////////////////////
                
                
                // Close the connection.
                sqlResult.close();
                sqlStatement.close();
                conn.close();
                
                
                //db job end
                
                
                } catch (Exception e) {
                    e.printStackTrace();
                  }
                
                
                if (access==true){
                	System.out.println("multipartInfo> Accesso effettuato si procede alla creazione della sessione");
                HttpSession session = request.getSession();
                
              
                //TODO
                // Print the current Session's ID 
                System.out.println("multipartInfo> Session ID:" + " " + session.getId()); 
                
                // Print the current Session's Creation Time 
                System.out.println("multipartInfo> Sessione Creata:" + " " + new Date(session.getCreationTime()) + "<br>"); 
                // Print the current Session's Last Access Time 
                System.out.println("multipartInfo> Sessione Ultimo accesso" + " " + new Date(session.getLastAccessedTime()));
                
                session.setAttribute(userstring, new Date(session.getLastAccessedTime()).toString());
                session.setAttribute("name", firstname+" "+lastname);
                //session.setMaxInactiveInterval(10*60);
                session.setMaxInactiveInterval(10*60);
                
                sessions.put(userstring, session);
                /// /// /// session check
                }
                
                }
                
                
                
            }else{
            	
            	String userstring = request.getParameter("username");
                String passstring = request.getParameter("password");
                
                out.write("<p>Username: "+userstring+"</p>");
                out.write("<p>Password: "+passstring+"</p>");
            	
            	
               // request.setAttribute("message","Sorry this Servlet only handles file upload request");
            }
        
            /*
            try {
            request.getRequestDispatcher("/result.jsp").forward(request, response);
            }
            catch (ServletException ex) {
                //request.setAttribute("message", "File Upload Failed due to " + ex);
                out.write("<p>ci spostiamo ad un'altra pagina</p>");
             }          
            */
            out.write("  ");

            System.out.println("Server> Comunicazione terminata");
        	
        	
    	}
        
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println(" ");
        System.out.println("dataExtractor> Salvataggio file con header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
	
    /**
     * utility per recuperare byte array
     */
    private byte[] getbytearray_privatekey(Part part) throws IOException {
    	
    	
    	
        String contentDisp = part.getHeader("RSA-field");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                if (token.substring(token.indexOf("=") + 2, token.length()-1)=="private_byte"){
                	
                	InputStream is = part.getInputStream();
                	byte[] returned = null;
                	is.read(returned);
                	return returned;
                }
            }
        }
        return null;
    }

    
    private double contrast_measure(Mat img)
    {
    Mat dx = new Mat();
    Mat dy = new Mat();
    //org.bytedeco.javacpp.opencv_imgproc.Sobel(arg0, arg1, arg2, arg3, arg4);
    org.bytedeco.javacpp.opencv_imgproc.Sobel(img,dx,org.bytedeco.javacpp.opencv_core.CV_32F,1,0);
    //Sobel(img,dy,0,1,3,org.bytedeco.javacpp.opencv_core.CV_32F);
    org.bytedeco.javacpp.opencv_imgproc.Sobel(img,dy,org.bytedeco.javacpp.opencv_core.CV_32F,0,1);
    org.bytedeco.javacpp.opencv_core.magnitude(dx,dy,dx);
    
    return org.bytedeco.javacpp.opencv_core.sumElems(dx).get(0);
    }
    
    
    
    private byte[] getbytearray_pass(Part part) throws IOException {
        String contentDisp = part.getHeader("RSA-field");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                if (token.substring(token.indexOf("=") + 2, token.length()-1)=="pass_byte"){
                	
                	InputStream is = part.getInputStream();
                	byte[] returned = null;
                	is.read(returned);
                	return returned;
                }
            }
        }
        return null;
    }
    
    static public boolean removeUser(String userstring) 
        	throws IOException {
    	userstring=userstring.replaceAll("\\s","");
    	//System.out.println(userstring);
    	//System.out.println(sessions);
    	HttpSession s = sessions.get(userstring);
        if (sessions.get(userstring)!=null)
        {
        	System.out.println("utente in fase di rimozione: "+ userstring); 
            
            sessions.remove(userstring);
            System.out.println("utente sloggato");
        	s.invalidate();
        	return true;
        }else {
        	System.out.println("<p>username precedentemente non loggato</p>");
        	
        	return false;
        }

    	}
    
    
}
