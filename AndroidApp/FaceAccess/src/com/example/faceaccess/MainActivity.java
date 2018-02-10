package com.example.faceaccess;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {
	Face[] detectedFaces;
    private static Camera mCamera;
    private CameraPreview mPreview;
    File mediaStorageDir;
    String root;
    File miaCartella;
    public int counter=0;
    EncryptionUtil encObject;
    String originalText;
    String cipherText;
    String plainText;
    Integer contatoreFoto=99;
    EditText edit1;
    EditText edit2;
    HttpResponse response_post;
    static String response_post_string;
    Bitmap[] sending_images;
    Bitmap send;
    Bitmap send_1;
    Bitmap send_2;
    TelephonyManager telephonyManager;
    PublicKey publicKey;
    PublicKey publicClientKey;
    PrivateKey privateClientKey;
    FrameLayout preview;
    String array[] = {"pre","user","password","name","surname","mail","imei","post"};
    
    
    //inizio variabili splash

    private LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    private LayoutParams textParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private MainActivity _this;
    private Handler mHandler;
   
    protected static final int FINISH_LOAD = 0 ;
    protected static final int START_LOAD = 1 ;
    protected static final int ABORT_LOAD = 2 ;
    Message msg;
    
    //fine variabili splash
    
    

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyController.getInstance().setActivity1(this);
		telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		try{
		

    	InputStream ins = getResources().openRawResource(R.raw.public_key);
    	ObjectInputStream public_server_stream = null;
    	public_server_stream = new ObjectInputStream(ins);
    	publicKey = (PublicKey) public_server_stream.readObject();
  

    	InputStream ins_client = getResources().openRawResource(R.raw.public_client_key);
    	ObjectInputStream public_client_stream = null;
    	public_client_stream = new ObjectInputStream(ins_client);
    	publicClientKey = (PublicKey) public_client_stream.readObject();
    	
    	InputStream ins_client_private = getResources().openRawResource(R.raw.private_client_key);
    	ObjectInputStream private_client_stream = null;
    	private_client_stream = new ObjectInputStream(ins_client_private);
    	privateClientKey = (PrivateKey) private_client_stream.readObject();
    	

    	
		} catch (Exception e) {
            
 		   e.printStackTrace();

 	   }   

        _this = this;
       
        mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                        switch (msg.what) {
                        case FINISH_LOAD:
                    			setContentView(R.layout.activity_main);
                    			
                    			// Create an instance of Camera
		                        mCamera = getCameraInstance();
		                            
		                        // Create our Preview view and set it as the content of our activity.
		                        mPreview = new CameraPreview(_this, mCamera);
		                        
		                        mPreview.setKeepScreenOn(true);
		                        //
		                        
		                        preview = (FrameLayout) findViewById(R.id.camera_preview);
		                        preview.addView(mPreview);
		                        mPreview.setBackground(getResources().getDrawable(R.drawable.rounded_inside));
		                        
		                        
		                        edit1 = (EditText)findViewById(R.id.provaform);
		                        edit2 = (EditText)findViewById(R.id.password);
		                        
                 		        
                                SplashScreen.sendMessage(SplashScreen.CLOSE_SPLASH);
                                break;
                        case START_LOAD:
		                          
                            
                                  initializing();
                                  break;
                        case ABORT_LOAD:
                                finish();
                        }

                }
        };
        startSplash();
		
		
		
        
	}

	
	
	

	
	
public static void setResponsePost(String response){
	MainActivity.response_post_string=response;
	//Toast.makeText(getContext(), "login: "+ response_post_string , Toast.LENGTH_LONG).show();
	
}
    
public void onBackPressed() {
    MyController.getInstance().closeAllActivities();
    super.onBackPressed();
}

public void postDataMulti2(View v) {
	
	if ((send!=null)&&(send_1!=null)&&(send_2!=null)){
	if (
			!(
					(edit1.getText().toString().equals(""))
					||
					(edit2.getText().toString().equals(""))
					)
					){
    
    
	final CountDownLatch latch = new CountDownLatch(1);
	
    new Thread() {
        @Override
        public void run() {
	
        	
			        	   
			        	   try {
			        		   
			        		   
			        		   
			        		   
			        		   
			        		   String crlf = "\r\n";
			        		   String twoHyphens = "--";
			        		   String boundary =  "*****";
			        		   
			        		   
			        		   URL url = new URL("http://192.168.0.250:8080/OpenCVTest/op");
				        	   HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				        	   
				        	   urlConnection.setReadTimeout(10000);
				        	   urlConnection.setConnectTimeout(15000);
				        	   urlConnection.setRequestMethod("POST");
				        	   urlConnection.setUseCaches(false);
				        	   urlConnection.setDoOutput(true);
				        	   urlConnection.setChunkedStreamingMode(0);
				        	   
				        	   

			        		  final String originalText = edit2.getText().toString();
			        		  ObjectInputStream inputStream = null;
			        		        
			        		  
			                    	  	
		                      byte[] passwordcifrata_byte = encObject.encrypt(originalText, publicKey);
		                      String password_cifrata_64 = Base64.encodeToString(passwordcifrata_byte, Base64.DEFAULT);
			                    	
	                    	  byte[] imei_byte = encObject.encrypt(telephonyManager.getDeviceId(), publicKey);
	                    	  String imei_64 = Base64.encodeToString(imei_byte, Base64.DEFAULT);
			                    	
	                    	  byte[] public_client_byte = publicClientKey.getEncoded();
				        	  String public_client_64 = Base64.encodeToString(public_client_byte, Base64.DEFAULT);
			                    	
				        	   urlConnection.setRequestProperty("Connection", "Keep-Alive");
				        	   //urlConnection.setRequestProperty("Cache-Control", "no-cache");
				        	   urlConnection.setRequestProperty("Cache-Control", "max-age=0");
				        	   urlConnection.setRequestProperty("username", edit1.getText().toString());
				        	   urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				        	  
				        	   
				        	   // start data wrapper
				        	   
				        	   
				        	   DataOutputStream request = new DataOutputStream(urlConnection.getOutputStream());

				        	   
				        	 //altri
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "public_client" + "\";filename=\"" + "public_client" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   			        	   
				        	   request.writeBytes(public_client_64);
				        	   
				        	   request.writeBytes(crlf);
				        	   
				        	   
				        	   
				        	   //altri
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "pass_byte" + "\";filename=\"" + "pass_byte" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   
			                   request.writeBytes(password_cifrata_64);
			                   
				        	   request.writeBytes(crlf);
				        	   
				        	 //altri
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "imei" + "\";filename=\"" + "imei" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   
				        	   request.writeBytes(imei_64);
				        	   request.writeBytes(crlf);
				        	   
				        	   
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "prova.png" + "\";filename=\"" + "prova.png" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   
				        	   
				        	   //start send data
				        	   
				        	   ByteArrayOutputStream out2 = new ByteArrayOutputStream();
				        	   send.compress(Bitmap.CompressFormat.PNG, 100, out2);
			                   byte[] jdata = out2.toByteArray();
			                   request.write(jdata);
								
			                   //end send data
			                   
				        	   request.writeBytes(crlf);
				        	   
				        	   //altri
				        	   
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "prova3.png" + "\";filename=\"" + "prova3.png" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   
				        	   
				        	   //start send data
				        	   
				        	   out2 = new ByteArrayOutputStream();
				        	   send_2.compress(Bitmap.CompressFormat.PNG, 100, out2);
			                   jdata = out2.toByteArray();
			                   request.write(jdata);
								
			                   //end send data
			                   
				        	   request.writeBytes(crlf);
				        	   
				        	   
				        	   
				        	   //altri
				        	   request.writeBytes(twoHyphens + boundary + crlf);
				        	   request.writeBytes("Content-Disposition: form-data; name=\"" + "prova2.png" + "\";filename=\"" + "prova2.png" + "\"" + crlf);
				        	   request.writeBytes(crlf);
				        	   
				        	   ByteArrayOutputStream out_1 = new ByteArrayOutputStream();
				        	   send_1.compress(Bitmap.CompressFormat.PNG, 100, out_1);
			                   byte[] jdata_1 = out_1.toByteArray();
			                   request.write(jdata_1);
				        	   
				        	   request.writeBytes(crlf);
				        	   request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
				        	   //end altri
				        	   
				        	   
				        	   
			        	     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			        	    
			        	     
			        	     int maxBufferSize = 1 * 1024 * 1024;
			        	     int bytesRead, bytesAvailable, bufferSize;
					            byte[] buffer;
			        	     // create a buffer of  maximum size
					            bytesAvailable = in.available();
					   
					            bufferSize = Math.min(bytesAvailable, maxBufferSize);
					            buffer = new byte[bufferSize];
					   
					            // read file and write it into form...
					            InputStreamReader isr = new InputStreamReader(in);
					            int charRead;
					            String str = "";
					            char[] inputBuffer = new char[bufferSize];          
					          
					                while ((charRead = isr.read(inputBuffer))>0)
					                {                    
					                    //---convert the chars to a String---
					                    //String readString = String.copyValueOf(inputBuffer, 0, charRead);
					                	String readString = String.copyValueOf(inputBuffer, 0, charRead);
					                    str += readString;
					                    inputBuffer = new char[bufferSize];
					             
					                }
					                
				        	     //String serverResponseMessage = urlConnection.getResponseMessage();
					                String serverResponseMessage = "";
				        	     serverResponseMessage+= str;
				        	     MainActivity.setResponsePost(serverResponseMessage);
			        	     
			        	   } 
			        	   catch (MalformedURLException ex) {
            
			        		   ex.printStackTrace();
			        		   
	            
			        		   Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
			        	   } catch (Exception e) {
	            
			        		   //e.printStackTrace();
			        		   response_post_string = "Server non raggiungibile";
			        	   }    
			        	    finally {
			        	     
			        	   }
			        	 
        	
        				
    
					    latch.countDown();
				}

				}.start();

				try {
				latch.await();
				} catch (InterruptedException ex) {} // return;

				//Toast.makeText(this, "login: "+ response_post_string , Toast.LENGTH_LONG).show();
				if (response_post_string.contains("connection_validate")){
					
					String[] tokens = response_post_string.split(";content=");
					int cc=0;
					for (String token : tokens) {
						
						 if ((cc!=0)&&(cc!=7)){
							 
	
								byte[] text_byte = Base64.decode(token, Base64.DEFAULT);
			              	  	String text_plain = encObject.decrypt(text_byte, privateClientKey);
			              	  	array[cc]=text_plain;
								//Toast.makeText(this, "token: "+ text_plain , Toast.LENGTH_LONG).show();
							 
						 }
						 
						 cc++;
			        }
					
					external();
					
				}else{
						
					Toast.makeText(this,  response_post_string , Toast.LENGTH_LONG).show();
					
				}
				    
				       //String reply = tokens[0]+tokens[7];
				       //Toast.makeText(this, "Risposta: "+ reply , Toast.LENGTH_LONG).show();
	}else{
		Toast.makeText(this,  "Inserire tutte le credenziali" , Toast.LENGTH_LONG).show();
	}
	
	
	
	
	}else{
		Toast.makeText(this,  "Acquisire il volto" , Toast.LENGTH_LONG).show();
	}
	
	
				} 


	
	
private void startSplash() {
        Intent intent = new Intent(this, SplashScreen.class);
        SplashScreen.setMainHandler(mHandler);
        startActivity(intent);
}
	
	
    
    private void initializing() {
        new Thread() {
                @Override
                public void run() {
                	
                	

            		
            		
            		
        			
        		  encObject = new EncryptionUtil();
        		  //
        		  Message msg = new Message();
                  msg.what = SplashScreen.SET_PROGRESS;
                  msg.arg1 = 49;
                  SplashScreen.sendMessage(msg);
                  if (!encObject.areKeysPresent()) {
    		          // Method generates a pair of keys using the RSA algorithm and stores it
    		          // in their respective files
    		        	encObject.generateKey();
    		        }
                  
                  try {
      		        // Check if the pair of keys are present else generate those.
      		        if (!encObject.areKeysPresent()) {
      		          // Method generates a pair of keys using the RSA algorithm and stores it
      		          // in their respective files
      		        	encObject.generateKey();
      		        }
      		        //versione senza salvataggio delle chiavi
      		        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      		        keyGen.initialize(512);
      		        KeyPair key = keyGen.generateKeyPair();
      		        

      		      } catch (Exception e) {
      		        e.printStackTrace();
      		      }
        		    //
        		    msg = new Message();
                    msg.what = SplashScreen.SET_PROGRESS;
                    msg.arg1 = 66;
                    SplashScreen.sendMessage(msg);
            	      
                    //
        		    msg = new Message();
                    msg.what = SplashScreen.SET_PROGRESS;
                    msg.arg1 = 82;
                    SplashScreen.sendMessage(msg);
                    
                    createDir();
                	
                	//            		    
                    msg = new Message();
                    msg.what = SplashScreen.SET_PROGRESS;
                    msg.arg1 = 100;
                    SplashScreen.sendMessage(msg);
                    
                    
                	//
                    
                    mHandler.sendEmptyMessage(FINISH_LOAD);
                    

                }

        }.start();
}
	
	

    
public void createDir(){
	root= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
	
	miaCartella = new File(root+"/FaceAccessDir2");
	miaCartella.mkdirs();
	
}




/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	     private SurfaceHolder mHolder;
	     //private Camera mCamera;

	 	 Paint drawingPaint;
		 boolean haveFace;
		 private PreviewCallback mPreviewCallback;
		 Rect vista;
		 Face [] facce;
		 private Rect r;
		 private int previewHeight = 0;
		 private int previewWidth = 0;
		 private int previewFormat = 0;
		 private byte[] mCallbackBuffer;
		 
		 //previewcallback
		 Camera.Parameters parameters;
		 Size size;
		 YuvImage image;
		 Rect rectangle;
		 Matrix matrix;
		 FaceDetector fd;
     	FaceDetector.Face [] arraydifacce = new FaceDetector.Face[5];
     	 PointF midpoint = new PointF();
     	 int [] fpx = null;
     	 int [] fpy = null;
     	 int [] ulpx = null;
     	 int [] ulpy = null;
     	 float [] eyedistance = null;
     	 int count = 0;
     	 float temp;
		 
    public CameraPreview(Context context, Camera camera) {
        super(context);
        //previewcallback
        parameters = camera.getParameters();
        size = parameters.getPreviewSize();
        rectangle = new Rect();
        rectangle.bottom = size.height;
        rectangle.top = 0;
        rectangle.left = 0;
        rectangle.right = size.width;
        matrix = new Matrix();
        matrix.postRotate(-90);
        
       // mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        vista = mHolder.getSurfaceFrame();
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        


        haveFace = false;
		drawingPaint = new Paint();
		drawingPaint.setColor(Color.WHITE);
		drawingPaint.setStyle(Paint.Style.STROKE); 
		drawingPaint.setStrokeWidth(5);
		   
        
    }
    
    

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
        	setWillNotDraw(false); 
            mCamera.setPreviewDisplay(holder);
          //  mCamera.setPreviewCallback(getPreviewCallback());
            
            
            
            

        } catch (IOException e) {
            //Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        mCamera.setPreviewCallback(null); 
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {

        	
        	//mHolder.setFixedSize(w, h);
            // Start the preview
            Parameters params = mCamera.getParameters();
            //params.setPreviewFormat(PixelFormat.JPEG);
            previewHeight = params.getPreviewSize().height;
            previewWidth = params.getPreviewSize().width;
            previewFormat = params.getPreviewFormat();
     
            // Crop the edges of the picture to reduce the image size
            r = new Rect(100, 100, previewWidth - 100, previewHeight - 100);
     
            mCallbackBuffer = new byte[1046800];
     
            mCamera.setParameters(params);
            mCamera.unlock();
            mCamera.reconnect();
            mCamera.setPreviewCallbackWithBuffer(getPreviewCallback());

        	mCamera.setDisplayOrientation(90);
        	
            mCamera.addCallbackBuffer(mCallbackBuffer);
            mCamera.startPreview();
            /* disabilitato prova 20140709
            startFaceDetection(); // start face detection feature
        	*/
        	
        	
        	
        	
        } catch (Exception e){
            //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    private PreviewCallback getPreviewCallback() {
        if (mPreviewCallback == null) {
            mPreviewCallback = new PreviewCallback() {
            	
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                	
                	//Toast.makeText(getContext(), "faccia? " + detectedFaces, Toast.LENGTH_LONG).show();
                	if(contatoreFoto<3){
                		
                	
                    // Here I would like to do something later on
                   // Camera.Parameters parameters = camera.getParameters();
                    //Size size = parameters.getPreviewSize();
                    image = new YuvImage(data, ImageFormat.NV21,
                            size.width, size.height, null);
                    
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    image.compressToJpeg(rectangle, 100, out2);
                    

                    
                    	//convertiamo in bmp per il facedetector
                    byte[] jdata = out2.toByteArray();
                    BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
                    bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap imaging = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);

                    
                    
                    //new Thread(new MyRunnable(contatoreFoto,imaging)).start();
                    
                   
                    // We rotate the same Bitmap
                    Bitmap bitmap = Bitmap.createBitmap(imaging, 0, 0, size.width, size.height, matrix, false);

                    // We dump the rotated Bitmap to the stream 

                    ByteArrayOutputStream rotatedStream = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.JPEG, 100, rotatedStream);

                   
            		//iniziamo il facedetector
		            	
		
						try 
							{
							fd = new FaceDetector(bitmap.getWidth() , bitmap.getHeight(), 5);
							count = fd.findFaces(bitmap, arraydifacce);
							} 
						catch (Exception e) 
							{
							}
		
						// check if we detect any faces
						if (count > 0) 
						{
							fpx = new int[count];
							fpy = new int[count];
							ulpx = new int[count];
							ulpy = new int[count];
							eyedistance = new float[count];
						
							for (int i = 0; i < count; i++) 
							{
								try 
									{
									arraydifacce[i].getMidPoint(midpoint);
									eyedistance[i]= arraydifacce[i].eyesDistance();
										fpx[i] = (int)midpoint.x;
										fpy[i] = (int)midpoint.y;
										temp = eyedistance[i]*9/9;
										//ulpx[i] = (int)midpoint.x - (int)temp;
										if ((midpoint.x - temp)<0) ulpx[i]=1; else {ulpx[i] = (int)(midpoint.x - temp);}
										//ulpy[i] = (int)midpoint.y - (int)eyedistance[i];
										if ((midpoint.y - eyedistance[i])<0) ulpy[i]=1; else {ulpy[i] = (int)(midpoint.y - eyedistance[i]*3/4);}
										
										
									} 
								catch (Exception e) 
									{
									}
							}
						}
		                    
                    //finisce il facedetector
				

                		////String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                		////String nomeFoto="TEST_"+timeStamp+".jpg";
                		
                		
                		////File f = new File(miaCartella, nomeFoto);
                		int bitmap_width = bitmap.getWidth();
                		int bitmap_height = bitmap.getHeight();
                		
                    	if(
                    			(count > 0) &&
                    			(ulpx[0]>((Integer)(bitmap_width/6)))&&
                    			(ulpy[0]>((Integer)(bitmap_height/12)))
                    			
                    			)
                    			
                    			{
                    		contatoreFoto++;
                    		
                    		try {
                        		////f.createNewFile();
                        		//write the bytes in file
                        		////FileOutputStream fo = new FileOutputStream(f);
                        		//fo.write(bytes.toByteArray());
                        		//scriviamo solo il rettangolo
                        		
                        		int larghezza=(int)(eyedistance[0]*4/2);
                        		if (ulpx[0]+(int)(eyedistance[0]*4/2)>bitmap.getWidth()) larghezza=bitmap.getWidth()-ulpx[0]-1;
                        		int altezza=(int)eyedistance[0]*5/2;
                        		//if (ulpy[0]+(int)eyedistance[0]*3>bitmap.getWidth()) larghezza=bitmap.getWidth()-ulpy[0]-1;
                        		if (ulpy[0]+(int)eyedistance[0]*5/2>bitmap.getHeight()) altezza=bitmap.getHeight()-ulpy[0]-1;
                        		
                        		
                        		Bitmap resizedbitmap=Bitmap.createBitmap(bitmap,ulpx[0],ulpy[0], larghezza, altezza);
                        		resizedbitmap = Bitmap.createScaledBitmap(resizedbitmap, 340, 424, true);
                        		//fine scrittura del rettangolo
                        		ByteArrayOutputStream out3 = new ByteArrayOutputStream();
                        		resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, out3);
                       		 	////fo.write(out3.toByteArray());
                        		////fo.flush();
                        		// remember close de FileOutput
                        		////fo.close();
                        		
                        		if(contatoreFoto==1){send = resizedbitmap;}
                        		else if(contatoreFoto==2){send_1 = resizedbitmap;}
                        		else if(contatoreFoto==3){send_2 = resizedbitmap;}
                        		
                        		
                        		}
                        		catch (Exception e){
                        		Toast.makeText(getContext(), "non funziona", Toast.LENGTH_SHORT).show();
                        		Toast.makeText(getContext(), "ulpx[0] "+ulpx[0], Toast.LENGTH_SHORT).show();
                        		Toast.makeText(getContext(), "ulpy[0] "+ulpy[0], Toast.LENGTH_SHORT).show();
                        		Toast.makeText(getContext(), "eyedistance[0] "+eyedistance[0], Toast.LENGTH_SHORT).show();
                        		
                        		}
                    		
                    		
                    		

                    	}//chiusura if
                		
                	
                	
                	


                    mCallbackBuffer = new byte[1046800];
                    mCamera.addCallbackBuffer(mCallbackBuffer);
                	}
                	
                }

            };
        }
        return mPreviewCallback;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	  public void setHaveFace(boolean h){
		   haveFace = h;
		  }
  
	  
    @Override
    protected void onDraw(Canvas canvas) {
    	
    	 int vHeight= vista.height();
		 int vWidth = vista.width();
		 
    	 Paint p = new Paint();
    	 int sweep_angle=360;
    	 
    	 if (contatoreFoto==99){p.setColor(Color.RED);}
    	 
    	 if (contatoreFoto==0){p.setColor(Color.RED);}
    	 else
    	 	{
    		 if (contatoreFoto==1){p.setColor(Color.MAGENTA);sweep_angle=120;}
    		 else
    		 	{
    			 if (contatoreFoto==2){p.setColor(Color.YELLOW);sweep_angle=240;}
    			 else
    				 {
    				 if (contatoreFoto==3){p.setColor(Color.GREEN);sweep_angle=360;}
    				 
    				 }
    			 
    		 	}
    	 	}
         
         DashPathEffect dashPath = new DashPathEffect(new float[]{15,10}, (float)1.0);

         p.setPathEffect(dashPath);
         p.setStyle(Style.STROKE);
         p.setStrokeWidth(5);
         
         //canvas.drawOval(new RectF((Integer)(vWidth/6), (Integer)(vHeight/12),(Integer)(vWidth*5/6), (Integer)(vHeight*3/4)), p);
         canvas.drawArc(new RectF((Integer)(vWidth/6), (Integer)(vHeight/12),(Integer)(vWidth*5/6), (Integer)(vHeight*3/4)), -90, sweep_angle, false, p);
         invalidate();
    	
    }
    

	 
		
    
    
    
    

}



@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
}

/** A safe way to get an instance of the Camera object. */
public static Camera getCameraInstance(){
    Camera c = null;
    try {
        c = Camera.open(1); // attempt to get a Camera instance
    }
    catch (Exception e){
        // Camera is not available (in use or does not exist)
    }
    return c; // returns null if camera is unavailable
}

public void addCounter(){
	this.counter= this.counter+1;
	
	
}
 

public int getCounter(){
	return this.counter;
	
}




public void deleteSession() {
	
	if (edit1.getText().toString().equals("")){
			Toast.makeText(this, "Inserire username", Toast.LENGTH_SHORT).show();
		}else{
	
	final CountDownLatch latch = new CountDownLatch(1);
	
    new Thread() {
        @Override
        public void run() {
	
			    // Create a new HttpClient and Post Header
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost("http://192.168.0.250:8080/OpenCVTest/out");
			
			    try {
			    	
			    	
			    	// -----    Single part
			        // Add your data
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			        nameValuePairs.add(new BasicNameValuePair("username", edit1.getText().toString()));
			    	
		            
		            
			    	
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        
			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(httppost);
			        
			        // Saves response
	                String line = "";
	                StringBuilder result = new StringBuilder();

	                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

	                while ((line = rd.readLine()) != null) {
	                        result.append(line);
	                }
	                rd.close();

	                response_post_string ="Sessione eliminata";
			        	
			    } catch (ClientProtocolException e) {
			        // TODO Auto-generated catch block
			         
			        
			    } catch (IOException e) {
			    	response_post_string = "Server non raggiungibile";
			        // TODO Auto-generated catch block
			    }
			    latch.countDown();
	        }
	
	}.start();

	try {
		latch.await();
	} catch (InterruptedException ex) {} // return;
	
	Toast.makeText(this, response_post_string, Toast.LENGTH_SHORT).show();
	
	}
} 



@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
        case R.id.training:
        	trainingActivity();
            return true;
        case R.id.delSess:
        	deleteSession();
            return true;
        case R.id.logtest:
        	external();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
}


public void trainingActivity(){
	Intent intent = new Intent(this, TrainingActivity.class);
	startActivity(intent);
	
}

public void external(){
	
	Intent intent = new Intent(this, ExternalView.class);
	intent.putExtra("strings", array);
	startActivity(intent);
	
}

public void preprocess(View v){
	if ((send!=null)&&(send_1!=null)&&(send_2!=null)){
		   send = sharpen(send,11);
		   send_1 = sharpen(send_1,11);
		   send_2 = sharpen(send_2,11);
	}
 
}



public static Bitmap sharpen(Bitmap src, double weight) {
    double[][] SharpConfig = new double[][] {
        { 0 , -2    , 0  },
        { -2, weight, -2 },
        { 0 , -2    , 0  }
    };
    ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
    convMatrix.applyConfig(SharpConfig);
    convMatrix.Factor = weight - 8;
    return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
}



public void reset(View v){
	_this.contatoreFoto = 0;
	
	
	Camera.Parameters parameters = mCamera.getParameters();
    Size size = parameters.getPreviewSize();
	mPreview.surfaceChanged(mPreview.getHolder(), parameters.getPreviewFormat(), size.width, size.height);
	
    
}





	 
}
