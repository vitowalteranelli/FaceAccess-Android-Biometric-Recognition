
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.IntBuffer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.*;




@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
				 maxFileSize=1024*1024*50,          // 50 MB
				 maxRequestSize=1024*1024*100)      // 100 MB
public class JavaCV extends HttpServlet {
	
	
		EncryptionUtil encObject;
		
	    String roottot = "/home/cheggynho/workspace/OpenCVTest";
	    String nomeapp = "OpenCVTest";
	    String webapps = "/webapps/";
	    String rootwebapp = System.getProperty("catalina.base")+webapps+nomeapp;

	    String rootworking_trash = "/home/cheggynho/working_trash/";
	    String desk = "desk/";
	   

	    
	    
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    	throws IOException {

    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	
    	 out.write("<html><body>");

         out.write("<h1>Ciao JavaCV</h1>");
         
         String output_folder = rootworking_trash+"/recog/";

         String trainingDir = rootworking_trash+"/recog/";
         Mat testImage = org.bytedeco.javacpp.opencv_highgui.imread(rootworking_trash+"/prova2.png", org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
         out.write("<p>Immagine di test caricata</p>");
         File root = new File(trainingDir);

         FilenameFilter imgFilter = new FilenameFilter() {
             public boolean accept(File dir, String name) {
                 name = name.toLowerCase();
                 return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
             }
         };

         File[] imageFiles = root.listFiles(imgFilter);
         out.write("<p>Array di file caricato</p>");
         MatVector images = new MatVector(imageFiles.length);

         Mat labels = new Mat(imageFiles.length, 1, org.bytedeco.javacpp.opencv_core.CV_32SC1);
         IntBuffer labelsBuf = labels.getIntBuffer();
         out.write("<p>Vettori istanziati</p>");
         int counter = 0;

         for (File image : imageFiles) {
             Mat img = org.bytedeco.javacpp.opencv_highgui.imread(image.getAbsolutePath(), org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
             out.write("<p>"+(counter+1)+" immagine caricata: "+img.cols()+" colonne, "+img.rows()+" righe ;");
             int label = Integer.parseInt(image.getName().split("\\-")[0]);
             String nome = image.getName().split("\\-")[1].split("_")[0];
             out.write(""  +(counter+1)+" etichetta caricata: "+nome+"</p>");
             images.put(counter, img);

             labelsBuf.put(counter, label);

             counter++;
         }
         out.write("<p>ciclo completato</p>");
         FaceRecognizer faceRecognizer = org.bytedeco.javacpp.opencv_contrib.createFisherFaceRecognizer();
         // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
         // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()
         out.write("<p>Recognizer caricato</p>");
         
         
         faceRecognizer.train(images, labels);
         faceRecognizer.save(rootworking_trash+"/recog/"+"eigenfaces_at.yml");
         out.write("<p>dati addestrati</p>");
         Mat img2= new Mat();
         org.bytedeco.javacpp.opencv_imgproc.resize(testImage, img2, new Size(340,424));
         org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img2, img2);
         int predictedLabel = faceRecognizer.predict(img2);

         System.out.println("Predicted label: " + predictedLabel);

         
         out.write("<h2>Sei l'utente numero "+predictedLabel+"</h2>");

         out.write("</body></html>");
	}
        
        
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        	throws ServletException, IOException {

        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	
        	out.write("<html><body>");
        	//// = funzionanti ma commentati
            ////out.write("<h1>Questo è un post response</h1>");       
            
            out.write("</body></html>");
        	
        	
        	
    	}
        

    
    
    
    void read_csv(String filename, MatVector images, int[] labels) {/*
    	String fn_csv = rootworking_trash+"/recog/"+"table.csv";
    	File csv_path = new File(fn_csv);
    	if (csv_path.getParentFile() != null) {
    		csv_path.getParentFile().mkdirs();
          }
    	
    	
    	
    	BufferedReader br = null;
    	String line = "";
    	String cvsSplitBy = ";";
     
    	try {
    		
    		long counter = 0;
    	    int label;

    	    
    	    IplImage grayImg;
     
    		br = new BufferedReader(new FileReader(fn_csv));
    		while ((line = br.readLine()) != null) {
     
    		        // use semicolumn as separator
    			String[] active_line = line.split(cvsSplitBy);
    			String file_path = rootworking_trash+"/recog/"+active_line[0];
    			File image = new File(file_path);
    			BufferedImage imgbuff =  ImageIO.read(image);
    			IplImage origImg = IplImage.createFrom(imgbuff);
    			Mat img = new Mat(origImg);
    			
    			
    			//images.put(counter, img);
    			
    			//images.add(Highgui.imread(active_line[0]));
    			//Mat row = new Mat(1, 1, CvType.CV_8U);  // 3 cols
    			//row.put(0, 0, Integer.parseInt(active_line[1]));
    			//labels.push_back(row);    
    			
    		      labels[(int)counter] = Integer.parseInt(active_line[1]);
    		      // Increase counter for next image:
    		      counter++;
    			//labels.add(Integer.parseInt(active_line[1]));
    			System.out.println("Line [path= " + active_line[0] 
                                     + " , label=" + active_line[1] + "]");
     
    		}
     
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
     
*/

    }
    
}
