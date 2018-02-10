import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;




@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
				 maxFileSize=1024*1024*50,          // 50 MB
				 maxRequestSize=1024*1024*100)      // 100 MB
public class Recognition extends HttpServlet {
	
	
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

         out.write("<h1>Ciao OpenCV "+Core.VERSION+"</h1>");
         
         String output_folder = rootworking_trash+"/recog/";
         
         String fn_csv = rootworking_trash+"/recog/"+"table.csv";
         
         
         List<Mat> images = new ArrayList<Mat>();
         Mat labels = new Mat(0,0,CvType.CV_8U);
         Mat row;
         
         //a.add(1);  // returns true

         // Read in the data. This can fail if no valid
         // input filename is given.
         try {
             read_csv(fn_csv, images, labels);
         } catch (Exception e) {
             e.printStackTrace();
           }
         
         // Quit if there are not enough images for this demo.
         if(images.size() <= 1) {
             String error_message = "This demo needs at least 2 images to work. Please add more images to your data set!";
             out.write(error_message+"<br />");
         }
         
         // Get the height from the first image. We'll need this
         // later in code to reshape the images to their original
         // size:
         int height = images.get(0).rows();
         
         // The following lines simply get the last images from
         // your dataset and remove it from the vector. This is
         // done, so that the training data (which we learn the
         // cv::FaceRecognizer on) and the test data we test
         // the model with, do not overlap.
         Mat testSample = images.get(images.size() - 1);
         double[] testLabel = labels.get(labels.rows(), 0);
         //int intTestLabel = (int)testLabel[0];
         images.remove(images.size() - 1);
         //labels.remove(labels.size() - 1);
         Mat labels2  = new Mat();
         labels2 = labels.rowRange(0,labels.rows()-1);
         
         // The following lines create an Eigenfaces model for
         // face recognition and train it with the images and
         // labels read from the given CSV file.
         // This here is a full PCA, if you just want to keep
         // 10 principal components (read Eigenfaces), then call
         // the factory method like this:
         //
         //      cv::createEigenFaceRecognizer(10);
         //
         // If you want to create a FaceRecognizer with a
         // confidence threshold (e.g. 123.0), call it with:
         //
         //      cv::createEigenFaceRecognizer(10, 123.0);
         //
         // If you want to use _all_ Eigenfaces and have a threshold,
         // then call the method like this:
         //
         //      cv::createEigenFaceRecognizer(0, 123.0);
         //
        ////// FaceRecognizer model = new EigenFaceRecognizer();
         
         
         
         FaceRecognizer model = org.bytedeco.javacpp.opencv_contrib.createEigenFaceRecognizer();
         //FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
         //CanvasFrame canvas;
         
         //Mat labels = new Mat(CvType.CV_8U);    // 1 cols, 1 rows
         //Mat row = new Mat(1, 1, CvType.CV_8U);  // 3 cols
         //row.put(0, 0, 2);
         //m.push_back(row);                           // 3 cols, 5 rows
         
         ///model.train(images, labels);
         
         
        // Create a face detector from the cascade file in the resources
        // directory.
        //CascadeClassifier faceDetector = new CascadeClassifier(rootworking_trash+"lbpcascade_frontalface.xml");
         CascadeClassifier faceDetector = new CascadeClassifier();
        String face_cascade_name = rootworking_trash+"haarcascade_frontalface_alt_tree.xml";
        
        
        
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
        	
        	out.write("<html><body>");
        	//// = funzionanti ma commentati
            ////out.write("<h1>Questo è un post response</h1>");       
            
            out.write("</body></html>");
        	
        	
        	
    	}
        

    
    
    
    void read_csv(String filename, List images, Mat labels) {
    	String fn_csv = rootworking_trash+"/recog/"+"table.csv";
    	File csv_path = new File(fn_csv);
    	if (csv_path.getParentFile() != null) {
    		csv_path.getParentFile().mkdirs();
          }
    	
    	
    	
    	BufferedReader br = null;
    	String line = "";
    	String cvsSplitBy = ";";
     
    	try {
     
    		br = new BufferedReader(new FileReader(fn_csv));
    		while ((line = br.readLine()) != null) {
     
    		        // use semicolumn as separator
    			String[] active_line = line.split(cvsSplitBy);
     
    			images.add(Highgui.imread(active_line[0]));
    			Mat row = new Mat(1, 1, CvType.CV_8U);  // 3 cols
    			row.put(0, 0, Integer.parseInt(active_line[1]));
    			labels.push_back(row);    
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
     


    }
    
}
