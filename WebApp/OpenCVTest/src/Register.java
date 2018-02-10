import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.IntBuffer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;


@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
				 maxFileSize=1024*1024*50,          // 50 MB
				 maxRequestSize=1024*1024*100)      // 100 MB
public class Register extends HttpServlet {
	
	
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

         out.write("<h1>Ciao OpenCV </h1>");
        // Create a face detector from the cascade file in the resources
        // directory.
        //CascadeClassifier faceDetector = new CascadeClassifier(rootworking_trash+"lbpcascade_frontalface.xml");
         CascadeClassifier faceDetector = new CascadeClassifier();
        String face_cascade_name = rootworking_trash+"haarcascade_frontalface_alt_tree.xml";
        
        
        /*
         if( !faceDetector.load( face_cascade_name ) ){ out.write("--(!)Error loading\n");};
        Mat image = Highgui.imread(rootworking_trash+"lena.jpg");

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        out.write("Numero di facce riconosciute: "+faceDetections.toArray().length+"<br />");

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 5);
        }

        // Save the visualized detection.
        String filename = rootwebapp+"/"+desk+"faceDetection.png";
        out.write("Sto scrivendo: faceDetection.png"+"<br />");
        
        Highgui.imwrite(filename, image);
        out.write("<img src=\""+desk+"faceDetection.png\" width=\"200\" />");
        out.write("</body></html>");
    	
    	
    	
    	/*
    	
    	
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
       // System.load(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        out.write("<html><body>");
        
        Mat image= Highgui.imread("/home/cheggynho/Immagini/163_by_e4v.jpg");
        Highgui.imwrite(rootwebapp+"/"+desk+"result.jpg",image);
        
        
        out.write("<h1>Ciao OpenCV"+Core.VERSION+System.getProperty("catalina.base")+"</h1>");
        //out.write("<h1>Ciao OpenCV</h1>"+request.getContextPath()+"m = " + m.dump());
        out.write("<img src=\""+desk+"result.jpg\" width=\"200\" />");
        
       // Gpu.resetDevice();
        out.write("</body></html>");
    	//out.write("<html><body><h1>GET</h1>"+"ciao </body></html>");
    	 * 
    	 * 
    	 * 
    	 * 
    	 */
        
	}
        
        
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        	throws ServletException, IOException {

        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	
        	System.out.println("<html><body>");
        	//// = funzionanti ma commentati
            ////out.write("<h1>Questo è un post response</h1>");
            
            if(ServletFileUpload.isMultipartContent(request)){
            	
            	String userstring = request.getHeader("username");
            	System.out.println("<p>Username: "+userstring+"</p>");
                String firststring = request.getHeader("firstname");
                System.out.println("<p>FirstName: "+firststring+"</p>");
                String laststring = request.getHeader("lastname");
                System.out.println("<p>LastName: "+laststring+"</p>");
                String mailstring = request.getHeader("mailaddress");
                System.out.println("<p>Mail: "+mailstring+"</p>");
                
                
                ////String passstring = request.getHeader("password");
            	////String private_base64 = request.getHeader("private");
            	////String lunghezza_cypher_orig = request.getHeader("cypher_orig");
            	////String lunghezza_private_orig = request.getHeader("private_orig");
            	////String plain = request.getHeader("plain");
                ////String algorithm = request.getHeader("algorithm");
                
                ////out.write("<p>Username: "+userstring+"</p>");
                ////out.write("<p>Password: "+passstring+"</p>");
                
              //// byte[] restoredBytes = Base64.decodeBase64(passstring);
              ////byte[] private_header_Bytes = Base64.decodeBase64(private_base64);
              ////   out.write("<p>lunghezza: "+restoredBytes.length+"</p>");
              ////  out.write("<p>lunghezza: "+private_header_Bytes.length+"</p>");
              ////  out.write("<p>lunghezza cypher_orig: "+lunghezza_cypher_orig+"</p>");
              ////  out.write("<p>lunghezza private_orig: "+lunghezza_private_orig+"</p>");
              ////  out.write("<p>plain: "+plain+"</p>");
              ////out.write("<p>algorithm: "+algorithm+"</p>");
                
                
                // gets absolute path of the web application
                // String applicationPath = request.getServletContext().getRealPath("");
                // constructs path of the directory to save uploaded file
                String uploadFilePath = rootworking_trash;
                  
                ////out.write("<p>1: "+uploadFilePath+"</p>");
                // creates the save directory if it does not exists
                File fileSaveDir = new File(uploadFilePath);
                ////out.write("<p>2</p>");
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                    out.write("<p>CHECK if</p>");
                }
                //System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
                 /* funziona ma non serve a nulla
                //prova stampa request
                File fileprova =new File(uploadFilePath + "request.txt") ;
                PrintWriter fileWriter = new PrintWriter(fileprova);  
                fileWriter.write(request.toString());
                fileWriter.close();
                //fine prova
                */
                String fileName = null;
                //Get all the parts from request and write it to the file on server
                for (Part part : request.getParts()) {
                ////	out.write("<p>3:"+part.toString()+"</p>");
                    fileName = getFileName(part);
                ////    out.write("<p>4: "+fileName+"</p>");
                    part.write(uploadFilePath + fileName);
                ////    out.write("<p>5</p>");
                }
                ////out.write("<p>File caricato con successo:" + fileName + "</p>");
                
                
                /*
                byte[] prova_byte = null;
                
                InputStream prova_stream = request.getPart("prova_melone").getInputStream();
                
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                //IOUtils.copy(is, baos);
                //IOUtils.closeQuietly(is);
                //IOUtils.closeQuietly(baos);
               
                //private_key = baos.toByteArray();
                
                String prova_melone = IOUtils.toString(prova_stream, "UTF-8");
                
              ////System.out.println("fatto"); 
                prova_byte = Base64.decodeBase64(prova_melone);
                String prova_melone2 =new String(prova_byte);
              ////System.out.println("fatto");
                ////out.write("<p>prova tostring "+prova_byte.toString()+" -- prova new string "+prova_melone2+"lunghezza: "+ prova_byte.length +"</p>");
                
                
                
                
                byte[] private_key = null;
                
                InputStream is = request.getPart("private_byte").getInputStream();
                
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                //IOUtils.copy(is, baos);
                //IOUtils.closeQuietly(is);
                //IOUtils.closeQuietly(baos);
               
                //private_key = baos.toByteArray();
                
                String private_string = IOUtils.toString(is, "UTF-8");
                
              ////System.out.println("fatto"); 
                private_key = Base64.decodeBase64(private_string);
                
                System.out.println("estratta chiave privata");
              ////out.write("<p>chiave privata "+private_key.toString()+" lunghezza: "+ private_key.length +"</p>");
                
                

                byte[] public_key = null;
                
                is = request.getPart("public_byte").getInputStream();
                
                String public_string = IOUtils.toString(is, "UTF-8");
                
                
                public_key = Base64.decodeBase64(public_string);
                
                System.out.println("estratta chiave pubblica");
              ////out.write("<p>chiave pubblica "+public_key.toString()+" lunghezza: "+ public_key.length +"</p>");
                
                
                byte[] pass_byte = null;
                
                is = request.getPart("pass_byte").getInputStream();
                
                //baos = new ByteArrayOutputStream();
                
                //IOUtils.copy(is, baos);
                //IOUtils.closeQuietly(is);
                //IOUtils.closeQuietly(baos);
               
                //pass_byte = baos.toByteArray();
                
                ///StringWriter scrittore = new StringWriter();
                ///IOUtils.copy(is, scrittore, "UTF-8");
                ///String theString = scrittore.toString();
                
                String myString = IOUtils.toString(is, "UTF-8");
                
                System.out.println("estratta password criptata");
                byte[] pass_byte2 = Base64.decodeBase64(myString);
                String prova_pass2 =new String(pass_byte2);
              ////out.write("<p>password "+prova_pass2+" lunghezza: "+ pass_byte2.length +"</p>");
                
                
                              
                is = request.getPart("password_cifrata").getInputStream();
                
                String password_cifrata_64 = IOUtils.toString(is, "UTF-8");
                
                System.out.println("estratta password criptata con public server");
                byte[] password_cifrata_byte = Base64.decodeBase64(password_cifrata_64);
                */
                
                byte[] public_key = null;
                InputStream is = request.getPart("public_client").getInputStream();
                String public_string = IOUtils.toString(is, "UTF-8");
                public_key = Base64.decodeBase64(public_string);
                System.out.println("estratta chiave pubblica client");
                
                
                byte[] pass_byte = null;
                is = request.getPart("pass_byte").getInputStream();
                String myString = IOUtils.toString(is, "UTF-8");
                System.out.println("estratta password criptata");
                byte[] pass_byte2 = Base64.decodeBase64(myString);
                String prova_pass2 =new String(pass_byte2);
                              
                is = request.getPart("imei").getInputStream();
                String imei_64 = IOUtils.toString(is, "UTF-8");
                System.out.println("estratto imei");
                byte[] imei_byte = Base64.decodeBase64(imei_64);
                
                byte[] public_client;
                String imei;
                
                try {
                	/*
                	if (!encObject.areKeysPresent()) {
    		        	encObject.generateKey();
    		        }
					*/
                	
                	byte[] privateKeyBytes;
                	//byte[] publicKeyBytes;
                	////EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(private_key);
                	////EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(private_header_Bytes);
                	////out.write("<p>01</p>");
                	KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	////out.write("<p>02</p>");
                	
                	////PrivateKey private_ = kf.generatePrivate(privateKeySpec);
                	PublicKey public_ = kf.generatePublic(new X509EncodedKeySpec(public_key));
                	////KeyPair key = new KeyPair(public_,private_);
                	////KeyPair key2; 
                	System.out.println("<p>sto per decriptare</p>");
                	
                	//final String plainText = encObject.decrypt(pass_byte, private_);
                	
                	
                	//final String plainText = EncryptionUtil.decrypt(pass_byte, private_);
                	//final String plainText = EncryptionUtil.decrypt(restoredBytes, private_);
                	byte[] cipherText = encObject.encrypt("AAA", public_);
                	////String plainText = encObject.decrypt(cipherText, private_);
                	////out.write("<p>Password decriptata "+plainText+"</p>");
                	////plainText = encObject.decrypt(pass_byte2, key.getPrivate());
                	////out.write("<p>Password decriptata "+plainText+"</p>");
                	
                	
                	ObjectInputStream inputStream = null;
                	
                    // Encrypt the string using the public key
                    inputStream = new ObjectInputStream(new FileInputStream(encObject.PUBLIC_KEY_FILE));
                    //out.write("<p>Apro public key file</p>");
                    PublicKey publicKey = (PublicKey) inputStream.readObject();
                    System.out.println("<p>letto e creato public</p>");
                    String prova = "prova";
                    byte[] cipherProva = encObject.encrypt(prova, publicKey);
                    
                    inputStream = new ObjectInputStream(new FileInputStream(encObject.PRIVATE_KEY_FILE));
                    System.out.println("<p>Aperto private key file</p>");
                    PrivateKey privateKey = (PrivateKey) inputStream.readObject();
                    System.out.println("<p>letto e creato private</p>");
                    String password_screen = encObject.decrypt(pass_byte2, privateKey);
                    System.out.println("<p>Password decriptata con private server "+password_screen+"</p>");
                    String imei_screen = encObject.decrypt(imei_byte, privateKey);
                    System.out.println("<p>Imei decriptato con private server "+imei_screen+"</p>");
                
                    public_client = public_.getEncoded();
                    imei = imei_screen;
		        
                
                //db job
                
                // This is needed to use Connector/J. It basically creates a new instance
                // of the Connector/J jdbc driver.
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                System.out.println("<p>instanzio l'oggetto JDBC<p>");
                // Open new connection.
                java.sql.Connection conn;

                System.out.println("<p>creo la connessione al DB<p>");
                conn = DriverManager.getConnection("jdbc:mysql://localhost/FaceAccessDB?user=root");
                
                
                
                // Generate the SQL query.
                PreparedStatement sqlStatement = conn.prepareStatement("SELECT public_client FROM devices WHERE application_code=? ;");
                sqlStatement.setString(1, imei);
                // Get the query results and display them.
                ResultSet sqlResult = sqlStatement.executeQuery();
                
                int counter_resultset = 0 ;
                
                while(sqlResult.next()) {

                	counter_resultset++;
                	byte[] publicKeyBytes = sqlResult.getBytes("public_client");
                	kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	PublicKey public_client_db = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                	System.out.println("estratta dal db la chiave pubblica del client");
             	   
             	   
                }
                
                if (counter_resultset==0){
                	
                	System.out.println("l'applicazione non è registrata nel DB");
                	// Generate the SQL query.
                	sqlStatement = conn.prepareStatement("INSERT INTO devices (application_code,public_client) VALUES (? , ?);");
                		
                	sqlStatement.setString(1, imei);
                	sqlStatement.setBytes(2, public_client);
                	// Get the query results and display them.
                	sqlStatement.execute();
                	System.out.println("Inserita tupla nel DB");
                	
                	
                }
                
                
                
                //////////////////////////////////////////////////
                
                
                
                
                
                String destination = rootworking_trash+"/recog";
                
                File root = new File(destination);

                FilenameFilter imgFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        name = name.toLowerCase();
                        return (name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"));
                    }
                };

                File[] imageFiles = root.listFiles(imgFilter);

                MatVector images = new MatVector(imageFiles.length);

                int counter = 0;
                int label =0;
                int labeluser=0;
                

                int maximum = 0;
                String nome = "";
                for (File image : imageFiles) {
                	label = Integer.parseInt(image.getName().split("\\-")[0]);
                    nome = image.getName().split("\\-")[1].split("_")[0];
                    
                    if (nome.equals(userstring)){counter++;labeluser=label;}
                    if (label>maximum){maximum=label;}
                    
                }
                
                
                
                
                
                
                destination = rootworking_trash+"/recog/";
                
                root = new File(uploadFilePath);

                imgFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        name = name.toLowerCase();
                        return name.startsWith("prova")&&(name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png"));
                    }
                };

                imageFiles = root.listFiles(imgFilter);

                images = new MatVector(imageFiles.length);
                int labelTOT;
                if (labeluser==0){labelTOT=maximum+1;}else{labelTOT=labeluser;}
                
                for (File image : imageFiles) {
                    Mat img = org.bytedeco.javacpp.opencv_highgui.imread(image.getAbsolutePath(), org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);

                    Mat img2= new Mat();
                    org.bytedeco.javacpp.opencv_imgproc.resize(img, img2, new Size(340,424));
                    
                    org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img2, img2);
                    
                    //org.bytedeco.javacpp.opencv_photo.fastNlMeansDenoising(img2, img2);
                    counter++;
                    org.bytedeco.javacpp.opencv_highgui.imwrite(destination+labelTOT+"-"+userstring+"_"+counter+".png",img2);
                    
                    
                    org.bytedeco.javacpp.opencv_highgui.imwrite(image.getAbsolutePath(), img);
                    
                }
                
                
                // Generate the SQL query.
                sqlStatement = conn.prepareStatement("SELECT * FROM users WHERE username=? ;");
                sqlStatement.setString(1, userstring);
                // Get the query results and display them.
                sqlResult = sqlStatement.executeQuery();
                
                counter_resultset = 0 ;
                
                while(sqlResult.next()) {

                	counter_resultset++;
                	System.out.println("username presente nel database");
             	   
             	   
                }
                
                if (counter_resultset==0){
                	
                	System.out.println("lo username non è presente nel database");
                	// Generate the SQL query.
                	sqlStatement = conn.prepareStatement("INSERT INTO users (username,firstname,lastname,mail,label,public) VALUES (? , ? , ? , ? , ? , ?);");
                		
                	sqlStatement.setString(1, userstring);
                	sqlStatement.setString(2, firststring);
                	sqlStatement.setString(3, laststring);
                	sqlStatement.setString(4, mailstring);
                	sqlStatement.setInt(5, labelTOT);
                	sqlStatement.setBytes(6, pass_byte2);
                	// Get the query results and display them.
                	sqlStatement.execute();
                	System.out.println("Inserita tupla nel DB");
                	
                	
                }
                
                
                String output_folder = rootworking_trash+"/recog/";

                String trainingDir = rootworking_trash+"/recog/";
                
                System.out.println("<p>Immagine di test caricata</p>");
                root = new File(trainingDir);

                imgFilter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        name = name.toLowerCase();
                        return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
                    }
                };

                imageFiles = root.listFiles(imgFilter);
                System.out.println("<p>Array di file caricato</p>");
                images = new MatVector(imageFiles.length);

                Mat labels = new Mat(imageFiles.length, 1, org.bytedeco.javacpp.opencv_core.CV_32SC1);
                IntBuffer labelsBuf = labels.getIntBuffer();
                System.out.println("<p>Vettori istanziati</p>");
                counter = 0;

                for (File image : imageFiles) {
                    Mat img = org.bytedeco.javacpp.opencv_highgui.imread(image.getAbsolutePath(), org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
                    //out.write("<p>"+(counter+1)+" immagine caricata: "+img.cols()+" colonne, "+img.rows()+" righe ;");
                    label = Integer.parseInt(image.getName().split("\\-")[0]);
                    //nome = image.getName().split("\\-")[1].split("_")[0];
                    //out.write(""  +(counter+1)+" etichetta caricata: "+nome+"</p>");
                    images.put(counter, img);

                    labelsBuf.put(counter, label);

                    counter++;
                }
                
                
                FaceRecognizer faceRecognizer = org.bytedeco.javacpp.opencv_contrib.createFisherFaceRecognizer();
                // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
                // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()
                //out.write("<p>Recognizer caricato</p>");
                
                
                faceRecognizer.train(images, labels);
                faceRecognizer.save(rootworking_trash+"/recog/"+"eigenfaces_at.yml");
                System.out.println("<p>dati addestrati</p>");
                
                
                
                
                
                /////////////////////////////////////////////////
                
                
                
                // Close the connection.
                sqlResult.close();
                sqlStatement.close();
                conn.close();
                
                
                //db job end
                
                
                } catch (Exception e) {
                    e.printStackTrace();
                  }
                
                
                
                
                /*
                byte[] pass_byte = null;
                
                is = request.getPart("private_byte").getInputStream();
                data = 0;
                baos = new ByteArrayOutputStream();
                while((data = is.read()) != -1) {
                	baos.write(data);
                	data = is.read();
                }
                pass_byte = baos.toByteArray();
                out.write("<p>password "+pass_byte.toString()+"</p>");
                
                try {
                	byte[] privateKeyBytes;
                	//byte[] publicKeyBytes;
                	EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(private_key);
                	out.write("<p>01</p>");
                	KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	out.write("<p>02</p>");
                	
                	////PrivateKey private_ = kf.generatePrivate(privateKeySpec);
                	//PublicKey public = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                	////out.write("<p>03</p>");
                	
                	
                	////final String plainText = encObject.decrypt(pass_byte, private_);
                	////out.write("<p>Password decriptata "+plainText+"</p>");
                
		        

            } catch (Exception e) {
              e.printStackTrace();
            }
                */
                /*
                //Get all the parts from request and write it to the file on server
                for (Part part : request.getParts()) {
                	out.write("<p>003:"+part.toString()+"</p>");
                	private_key = getbytearray_privatekey(part);
                    out.write("<p>004: "+private_key.toString()+"</p>");
                }
                out.write("<p>Chiave privata caricata con successo</p>");
                
                
                byte[] pass_byte = null;
                //Get all the parts from request and write it to the file on server
                for (Part part : request.getParts()) {
                	out.write("<p>003:"+part.toString()+"</p>");
                	pass_byte = getbytearray_pass(part);
                    out.write("<p>004: "+pass_byte.toString()+"</p>");
                    
                }
                out.write("<p>Password criptata caricata con successo</p>");
                
                
                
                
                */
                
                
                
                //request.setAttribute("message", fileName + " File uploaded successfully!");
                //getServletContext().getRequestDispatcher("/response.jsp").forward(request, response);
            	
                
                
                
                
                
            	
            	/*
            	out.write("<p>1</p>");
            	 File file ;
            	 int maxFileSize = 5000 * 1024;
            	 int maxMemSize = 5000 * 1024;
            	   
            	 DiskFileItemFactory factory = new DiskFileItemFactory();

             	out.write("<p>1</p>");
                 // maximum size that will be stored in memory
                 factory.setSizeThreshold(maxMemSize);
                 

             	out.write("<p>1</p>");
                 // Location to save data that is larger than maxMemSize.
                 factory.setRepository(new File("c:\\temp"));

             	out.write("<p>1</p>");
                 // Create a new file upload handler
                 ServletFileUpload upload = new ServletFileUpload(factory);

             	out.write("<p>1</p>");
                 // maximum file size to be uploaded.
                 upload.setSizeMax( maxFileSize );

             	out.write("<p>1</p>");
                 try{ 
                	 
                	 List<FileItem> fields = upload.parseRequest(request);
                     // Parse the request to get file items.
                     List fileItems = upload.parseRequest((RequestContext) request);

                 	out.write("<p>1</p>");
                     // Process the uploaded file items
                     Iterator i = fileItems.iterator();

                 	out.write("<p>1</p>");
                     out.println("<html>");
                     out.println("<head>");
                     out.println("<title>JSP File upload</title>");  
                     out.println("</head>");
                     out.println("<body>");
                     while ( i.hasNext () ) 
                     {
                     	out.write("<p>1</p>");
                        FileItem fi = (FileItem)i.next();
                        if ( !fi.isFormField () )	
                        {
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        String fileName = fi.getName();
                        boolean isInMemory = fi.isInMemory();
                        long sizeInBytes = fi.getSize();
                        // Write the file
                        if( fileName.lastIndexOf("\\") >= 0 ){
                        file = new File( rootworking_trash + 
                        fileName.substring( fileName.lastIndexOf("\\"))) ;
                        }else{
                        file = new File( rootworking_trash + 
                        fileName.substring(fileName.lastIndexOf("\\")+1)) ;
                        }
                        fi.write( file ) ;
                        out.println("Uploaded Filename: " + rootworking_trash + 
                        fileName + "<br>");
                        }
                     }
                     out.println("</body>");
                     out.println("</html>");
                  }catch(Exception ex) {
                     System.out.println(ex);
                  }
                 */
                 
                 
            	 ////////////////////////////////////////////////////////////
            	   /*
                try {
                	out.write("<p>1</p>");
                    List<FileItem> multiparts = new ServletFileUpload(
                                             new DiskFileItemFactory()).parseRequest((RequestContext) request);
                    out.write("<p>2</p>");
                    for(FileItem item : multiparts){
                    	out.write("<p>3</p>");
                        if(!item.isFormField()){
                            String name = new File(item.getName()).getName();
                            item.write( new File(rootworking_trash + name));
                        }
                    }
               
                   //File uploaded successfully
                   //request.setAttribute("message", "File Uploaded Successfully");
                    out.write("<p>File caricato con successo</p>");
                    
                } catch (Exception ex) {
                   //request.setAttribute("message", "File Upload Failed due to " + ex);
                   out.write("<p>caricamento fallito</p>");
                }          
             */
                
                /*
                try {
                	byte[] privateKeyBytes;
                	//byte[] publicKeyBytes;
                	KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
                	PrivateKey private_ = kf.generatePrivate(new PKCS8EncodedKeySpec(private_key));
                	//PublicKey public = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                	
                	
		        final String plainText = encObject.decrypt(pass_byte, private_);
		        out.write("<p>Password decriptata "+plainText+"</p>");
                
		        

            } catch (Exception e) {
              e.printStackTrace();
            }
            
                
                
                */
                
               
                
                
                
                
                
                
                
                
                
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
            out.write("Dati registrati con successo");
        	
        	
        	
    	}
        
    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
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
    
    
}
