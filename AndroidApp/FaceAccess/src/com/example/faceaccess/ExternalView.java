package com.example.faceaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExternalView extends Activity {

	String[] tokens;
	static String response_post_string;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyController.getInstance().setActivity3(this);
		setContentView(R.layout.activity_external_view);
		TextView tvhead = (TextView)findViewById(R.id.textView1);
		Bundle extras = getIntent().getExtras();
		tokens = extras.getStringArray("strings");
		
		
		/*
		String[] columns = new String[] { "_id", "item", "description" };

		MatrixCursor matrixCursor= new MatrixCursor(columns);
		startManagingCursor(matrixCursor);

		matrixCursor.addRow(new Object[] { 1, "Item A", "...." });
		 */
		
		String[] columns = new String[] { "_id", "item", "description" };

		MatrixCursor matrixCursor= new MatrixCursor(columns);
		startManagingCursor(matrixCursor);

		matrixCursor.addRow(new Object[] { 1, "***** Profilo Utente *****", " " });
		matrixCursor.addRow(new Object[] { 1, "Nome Utente: ", tokens[1] });
		matrixCursor.addRow(new Object[] { 1, "Password: ", tokens[2] });
		matrixCursor.addRow(new Object[] { 1, "Nome: ", tokens[3] });
		matrixCursor.addRow(new Object[] { 1, "Cognome: ", tokens[4] });
		matrixCursor.addRow(new Object[] { 1, "Mail: ", tokens[5] });
		matrixCursor.addRow(new Object[] { 1, "IMEI: ", tokens[6] });
		
		String head = tokens[3]+" "+tokens[4];
		
		if (head.length()>15){
			
			float var = (head.length()/15);
			int size = Math.round(15/var);
			tvhead.setTextSize(size+10);
		}
		
		tvhead.setText(head);
		
		
		
		
		//the XML defined views which the data will be bound to
		  int[] to = new int[] {
		    R.id.num_item,
		    R.id.item,
		    R.id.Valueitem
		  };
		
		  //create the adapter using the cursor pointing to the desired data
		  //as well as the layout information
		  SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
		    this, R.layout.activity_listview,
		    matrixCursor,
		    columns,
		    to,
		    0);
		 
		  ListView listView = (ListView) findViewById(R.id.listView1);
		  // Assign adapter to ListView
		  listView.setAdapter(dataAdapter);
		
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.external_view, menu);
		return true;
	}
*/
	public void onBackPressed() {
        MyController.getInstance().closeAllActivities();
        super.onBackPressed();
    }
	public void home(View v){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
	}
	
	public void home(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
	}
	

	

	public void postData(View v) {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
	    new Thread() {
	        @Override
	        public void run() {
		
				    // Create a new HttpClient and Post Header
		        	HttpParams params = new BasicHttpParams();
		            HttpConnectionParams.setConnectionTimeout(params, 3000);
		            HttpConnectionParams.setSoTimeout(params, 10000);
				    HttpClient httpclient = new DefaultHttpClient(params);
				    HttpPost httppost = new HttpPost("http://192.168.0.250:8080/OpenCVTest/out");
				
				    try {
				    	
				    	
				    	// -----    Single part
				        // Add your data
				        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				        nameValuePairs.add(new BasicNameValuePair("username", tokens[1]));
				    	
			            
			            
				    	
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
	
				        
				        	
				    } catch (ClientProtocolException e) {
				        // TODO Auto-generated catch block
				         
				        
				    } catch (IOException e) {
				        // TODO Auto-generated catch block
				    	response_post_string = "Server non raggiungibile";
				    }
				    latch.countDown();
		        }
		
		}.start();
	
		try {
			latch.await();
		} catch (InterruptedException ex) {} // return;
		if (response_post_string!=null){Toast.makeText(this,  response_post_string , Toast.LENGTH_LONG).show();}
		home();
	} 
	

}
