import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_AA;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRectangle;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;


public class Crop extends HttpServlet  {

	
    String roottot = "/home/cheggynho/workspace/OpenCVTest";
    String nomeapp = "OpenCVTest";
    String webapps = "/webapps/";
    String rootwebapp = System.getProperty("catalina.base")+webapps+nomeapp;

    String rootworking_trash = "/home/cheggynho/working_trash/";
    String desk = "desk/";
    static{ System.load("/home/cheggynho/opencv_built/lib/libopencv_java249.so"); }
    
    private IplImage grayImage;


public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
	
	
	String FILE_NAME = "14-Pasquale_10";
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();
	
	 out.write("<html><body>");
	 Mat img = org.bytedeco.javacpp.opencv_highgui.imread(rootworking_trash+"recog/"+FILE_NAME+".png", org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);

	 org.bytedeco.javacpp.opencv_highgui.imwrite(rootworking_trash+"recog/"+FILE_NAME+".bmp",img);
	 
	 /*
     out.write("<h1>Ciao OpenCV "+Core.VERSION+"</h1>");
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
    Mat image2 = new Mat();
    // Draw a bounding box around each face.
    for (Rect rect : faceDetections.toArray()) {
    	image2 = new Mat(image,rect);
        Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 5);
        
    }

    // Save the visualized detection.
    String filename = rootwebapp+"/"+desk+"faceDetection.png";
    out.write("Sto scrivendo: faceDetection.png"+"<br />");
    
    Highgui.imwrite(filename, image2);
    out.write("<img src=\""+desk+"faceDetection.png\" width=\"200\" />");
    */
    
    
	// create temp storage, used during object detection
     CvMemStorage storage = CvMemStorage.create();

     // instantiate a classifier cascade for face detection
     CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(rootworking_trash+"haarcascade_frontalface_alt_tree.xml"));
     System.out.println("Detecting faces...");

     //org.bytedeco.javacpp.opencv_imgproc.resize(img, img2, new Size(340,424));
     
     //org.bytedeco.javacpp.opencv_imgproc.equalizeHist(grayImage, img2);
     IplImage src_img = org.bytedeco.javacpp.opencv_highgui.cvLoadImage(rootworking_trash+"recog/"+FILE_NAME+".bmp", 1);
     IplImage grayImage = IplImage.create(src_img.width(),
    		 src_img.height(), org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U, 1);

     org.bytedeco.javacpp.opencv_imgproc.cvCvtColor( src_img,grayImage, org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY);
     org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist(grayImage, grayImage);

     
     
     CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage,1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

     cvClearMemStorage(storage);

     // draw thick yellow rectangles around all the faces
     int total = faces.total();
     System.out.println("Found " + total + " face(s)");
     int SCALE =1;
     for (int i = 0; i < total; i++) {

             CvRect r = new CvRect(cvGetSeqElem(faces, i));
             cvRectangle(src_img, cvPoint( r.x()*SCALE, r.y()*SCALE ),cvPoint( (r.x() + r.width())*SCALE,(r.y() + r.height())*SCALE ),CvScalar.RED, 6, CV_AA, 0);

             String strRect = String.format("CvRect(%d,%d,%d,%d)", r.x(), r.y(), r.width(), r.height());
            
             System.out.println(strRect);
             //undo image scaling when calculating rect coordinates
     }
    
     String filename = rootwebapp+"/"+desk+"faceDetection.png";
     if (total > 0) {
             System.out.println("Saving marked-faces version of " + " in " + filename);

             cvSaveImage(filename, src_img);
             cvSaveImage(rootworking_trash+"recog/"+FILE_NAME+".jpg", src_img);
     }

    
	 
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
    
	
	
	
	
	
	
}
