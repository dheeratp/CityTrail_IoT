/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.bagi.levente.pedometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.BroadcastReceiver;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.getpebble.android.kit.PebbleKit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentFilter;

public class Pedometer extends Activity implements OnClickListener {
	private static final String TAG = "Treasure Trail";
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	public static int coinsValue;


	private TextView mStepValueView;
//	private TextView mPaceValueView;
//	private TextView mDistanceValueView;
//	private TextView mSpeedValueView;
//	private TextView mCaloriesValueView;
//	TextView mDesiredPaceView;
	public static int mStepValue;
	public static int parentcoinsValue;
	public TextView mChildStepCountView;
	//public TextView mChildGoalValueView;
	public TextView mChildCoinView;

	//public TextView mParentGoalValueView;
	public static int parentGoalValue;
	public TextView mParentCoinView;
//	private int mPaceValue;
//	private float mDistanceValue;
//	private float mSpeedValue;
	public static int mCaloriesValue;
//	private float mDesiredPaceOrSpeed;
	private int mMaintain;
	private boolean mIsMetric;
	private float mMaintainInc;
	private boolean mQuitting = false; // Set when user selected Quit from menu,
										// can be used by onPause, onStop,
										// onDestroy

	// START : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND
	// ANIMATION CHANGES
	private static final UUID KIDSENSE_APP_UUID = UUID.fromString("07d87811-510f-48f2-b723-6bcfc4db9a40");
	public static int childstepcount = 0;
	public static int stepGoal=0;
	public static int childcoinValue=0;
	public static int totalsteps = 0;
	public static int todayssteps = 0;
	private TextView textView;
	private SensorManager mSensorManager;
	private Sensor mStepCounterSensor;
	private Sensor mStepDetectorSensor;
	int notificationCount;
	private WakeLock mWakeLock = null;
	private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
	public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
	public Button startbutton;
	// END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION
	// CHANGES

	//Added for storing stepcount and coin data to SharedPreferences
	SharedPreferences state;
	SharedPreferences.Editor stateEditor;
	
	
	//public static final String TTDataStore = "TTDataStore" ;
	public static final String parentStepCountKey = "parentStepCountKey"; 
    public static final String parentCoinsKey = "parentCoinsKey"; 
    public static final String childStepCountKey = "childStepCountKey"; 
    public static final String childCoinsKey = "childCoinsKey"; 
	
    //Changes for IoT
    JSONObject jsonObject;
    static String url;
    URLConnection urlConn;
    DataOutputStream printout;
    
    /**
	 * True, when service is running.
	 */
	private boolean mIsRunning;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[ACTIVITY] onCreate");
		super.onCreate(savedInstanceState);
		
		
		url="http://russet.ischool.berkeley.edu:8080/users";
		mStepValue = 0;
		parentGoalValue =100;
		//mPaceValue = 0;

		setContentView(R.layout.main);
		
		Button clickButton = (Button) findViewById(R.id.checkin);
		clickButton.setOnClickListener(this);

		mUtils = Utils.getInstance();
		
		
		mStepValueView = (TextView) findViewById(R.id.step_value);
		mParentCoinView = (TextView) findViewById(R.id.parent_coins);
		//mParentGoalValueView = (TextView) findViewById(R.id.parent_goal);
		mChildStepCountView = (TextView) findViewById(R.id.log_data_text_view);
		//mChildGoalValueView = (TextView) findViewById(R.id.child_goal);
		mChildCoinView = (TextView) findViewById(R.id.child_coins);
		
	
		
		state = getSharedPreferences("state", 1);
		stateEditor = state.edit();
		int val1=0, val2=0, val3=0, val4=0;
	      if (state.contains(parentStepCountKey))
	      {
	    	  Log.i("1","parentStepCountKey="+parentStepCountKey);
	    	   val1 = state.getInt(parentStepCountKey, 0);
	    	  mStepValueView.setText(String.valueOf(val1));

	      }
	      if (state.contains(parentCoinsKey))
	      {
	    	  Log.i("2","parentCoinsKey="+parentCoinsKey);
	    	 val2 =state.getInt(parentCoinsKey, 0);
	    	  mParentCoinView.setText(String.valueOf(val2));

	      }
	      if (state.contains(childStepCountKey))
	      {
	    	  Log.i("3","childStepCountKey="+childStepCountKey);

	    	   val3=state.getInt(childStepCountKey, 0);
	    	  mChildStepCountView.setText(String.valueOf(val3));

	      }
	      if (state.contains(childCoinsKey))
	      {
	    	  Log.i("4","childCoinsKey="+childCoinsKey);

	    	   val4=state.getInt(childCoinsKey, 0);
	    	  mChildCoinView.setText(String.valueOf(val4));

	      }
	      
	      Log.i("Dheera-In onCreate()..values are"," val1="+ val1+" val2="+val2+" val3="+val3+" val4="+val4);
	      
	  

	}
	
	public void onClick(View v) {
	    switch (v.getId()) {
	        case  R.id.checkin: {
	        	  Log.i("Dheera-In onclick","CHECKIN button clicked");
	            
	            new CheckinTask().execute(url);
	            break;
	        }

	        case R.id.checkout: {
	            // do something for button 2 click
	            break;
	        }

	        //.... etc
	    }
	}
	
	
	class CheckinTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	        
	    	
	  	HttpClient httpclient = new DefaultHttpClient();
	       
	        String responseString = null;
	        	//POSTING STEPCOUNT
	   	        	    
	   	        	 JSONObject add_payload = new JSONObject();
	   	        	
	   	        	
	   	        	 JSONObject add_payload1 = new JSONObject();
	   	        	try {
						add_payload1.put("displayName","JohnnyWalker");
					 
	   	        	
	   	        	add_payload1.put("objectType", "person");
	   	        	
	   	        	add_payload.put("actor", add_payload1);
	   	        	add_payload.put("verb", "checkin");
	   	        	
	   	        	
   	        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
   	        	String output = formatter.format(new Date());
	   	    	
	   	    	System.out.println("Date = " + output);
	   	        
	   	        	
	   	        	add_payload.put("published", output);
	   	        	
		   	         JSONObject add_payload3 = new JSONObject();
		   	         add_payload3.put("objectType","place");
		   	        	
		   	         add_payload3.put("id", "http://example.org/berkeley/southhall/206");
		   	      add_payload3.put("displayName", "Meeting room at South Hall, UC Berkeley");
		   	   JSONObject add_payload4 = new JSONObject();
		   	add_payload4.put("latitude", 34.34);
		   	add_payload4.put("longitude", -127.23);
		   	add_payload4.put("altitude", 100.05);
		   	add_payload3.put("position",add_payload4);
		   	
		   	
		   	
		    JSONObject add_payload5 = new JSONObject();
		    add_payload5.put("locality","Berkeley");
		    add_payload5.put("region", "CA");
		    
		    add_payload3.put("address",add_payload5);
		    
		    JSONArray jsonArray1 = new JSONArray();
        	jsonArray1.put("room");
        	jsonArray1.put("meeting");
        	
        	add_payload3.put("descriptor-tags", jsonArray1);
        	
        	add_payload.put("object", add_payload3);
        	
        	  JSONObject add_payload6 = new JSONObject();
        	  add_payload6.put("displayName", "BerkeleyMeetingRoom");
        	  add_payload.put("provider", add_payload6);
		   	   
//		   	      {
//		   	    	 "actor": {
//		   	    	   "displayName": "Actor1",
//		   	    	   "objectType": "person"
//		   	    	 },
//		   	    	 "verb": "checkin",
//		   	    	 "published": "2015-06-07T19:31:55.000Z",
//		   	    	 "object": {
//		   	    	   "objectType": "place",
//		   	    	   "id": "http://example.org/berkeley/southhall/206"
//		   	    	   "displayName": "Meeting room at South Hall, UC Berkeley",
//		   	    	   "position": {
//		   	    	     "latitude": 34.34,
//		   	    	     "longitude": -127.23,
//		   	    	     "altitude": 100.05
//		   	    	   },
//		   	    	   "address": {
//		   	    	     "locality": "Berkeley",
//		   	    	     "region": "CA",
//		   	    	   },
//		   	    	   "descriptor-tags": [
//		   	    	     "room",
//		   	    	     "meeting"
//		   	    	   ]
//		   	    	 },
//		   	    	 "provider": {
//		   	    	   "displayName": "BerkeleyMeetingRoom"
//		   	    	 }
//		   	    	}
	   	        	    
		   	         
			   	      HttpPost httpost3 = new HttpPost("http://russet.ischool.berkeley.edu:8080/activities");
		   	        	 //passes the results to a string builder/entity
	   	        	 StringEntity se3 = new StringEntity(add_payload.toString());
	   	        	Log.i("IOT ...payload for CheckinTask is", add_payload.toString());
	   	        	  //sets the post request as the resulting string
		   	        	    httpost3.setEntity(se3);
	   	        	  //sets a request header so the page receving the request
		   	        	    //will know what to do with it
	   	        	    httpost3.setHeader("Content-Type", "application/stream+json");
	  	        	    Log.i("IOT CHECKIN DOINBACKGROUND", "Before execute...httpclient= "+httpclient);
	   	        	  //Handles what is returned from the page 
	   	        	    ResponseHandler responseHandler3 = new BasicResponseHandler();
	        	  
	   	        	    //UNCOMMENT THE FOLLOWING IF YOU WANT TO PUBLISH YOUR SERVICE AGAIN
	 	        	    httpclient.execute(httpost3, responseHandler3);
	   	        	    
	        	Log.i("IOT CHECKIN DOINBACKGROUND", "After write ");
	    }
	        	catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        Log.i("IOT OnPostExecute", "result is "+result);
	        //Do anything with response..
	        
	        
	    }
	}

	

	@Override
	protected void onStart() {
		Log.i(TAG, "[ACTIVITY] onStart");
		int v1=state.getInt(parentStepCountKey, 0);
		int v2=state.getInt(parentCoinsKey, 0);
		int v3=state.getInt(childStepCountKey, 0);
		int v4=state.getInt(childCoinsKey, 0);
		Log.i("PEDOMETER ONSTART()"," v1="+v1+" v2="+v2+" v3"+v3+" v4="+v4);
		
		super.onStart();
	}

	@Override
	protected void onResume() {

		super.onResume();
		final Handler handler = new Handler();
		
		Log.i("DHEERA-", "Inside onResume()");
		// start:code for datalogging from pebble
		mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(KIDSENSE_APP_UUID) {
			@Override
			public void receiveData(android.content.Context context,
					java.util.UUID logUuid, java.lang.Long timestamp,
					java.lang.Long tag, java.lang.Long data) {
				
				Toast.makeText(Pedometer.this, "Synced with Pebble.",Toast.LENGTH_SHORT).show();
				
				//Log.i("DHEERA", "Inside receiveData()...tag="+tag);
				// mDisplayText.append("\n");

				// mDisplayText.append("Footstep ");
				// mDisplayText.append(data);
				if(tag==4660)
				{
			
				childstepcount = data.intValue();
				//Log.i("DHEERA", "child step count=" + data.intValue());
				}
				else if(tag==4661)
				{
					stepGoal = data.intValue();
					//Log.i("DHEERA", "step goal =" + data.intValue());
					

				}
				else if(tag==4662)
				{
					childcoinValue = data.intValue();
					//Log.i("DHEERA", "coins collected =" + data.intValue());
					
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						updateUi();
					}
				});
			}
			
			@Override
			public void onFinishSession(Context context, UUID logUuid, Long timestamp, Long tag) {
			  super.onFinishSession(context, logUuid, timestamp, tag);

			  if(tag==4660)
				{
			  	//Calls for IoT
				Log.i("@@@@@@@@@@@@DHEERA", "onFinishSession!!!" + state.getInt(childStepCountKey, 0));
				
				 new RequestTask().execute(url);
				}
			  
			  	stateEditor.putInt("childStepCountKey", childstepcount);
				stateEditor.putInt("childCoinsKey", childcoinValue);
				stateEditor.commit();
				
				
			}
			
		};
		
		//Display pebble values even when pebble is not currently in sync with the phone
		if(childstepcount==0)
		{
			mChildStepCountView.setText("0"+" steps");
		}
		else
		{
			mChildStepCountView.setText(childstepcount+" steps");
		}
	
		if(childcoinValue==0)
		{
			mChildCoinView.setText("0 coins");
		}
		else
		{
			mChildCoinView.setText("Coins - "+childcoinValue);
		}

		
		PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);
		//PebbleKit.requestDataLogsForApp(this, KIDSENSE_APP_UUID);

		// end: code for datalogging from pebble

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);

		mUtils.setSpeak(mSettings.getBoolean("speak", false));

		// Read from preferences if the service was running on the last onPause
		mIsRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (!mIsRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsRunning) {
			bindStepService();
		}

		mPedometerSettings.clearServiceRunning();
		
		mIsMetric = mPedometerSettings.isMetric();

	}

	// Added by Dheera for phone's accelerometer part and datalogging from
	// pebble
	private void updateUi() {
		
		if(childstepcount==0)
		{
			mChildStepCountView.setText("0"+" steps");
		}
		else
		{
			mChildStepCountView.setText(childstepcount+" steps");
		}
		
		if(childcoinValue==0)
		{
			mChildCoinView.setText("0 coins");
		}
		else
		{
			mChildCoinView.setText("Coins - "+childcoinValue);
		}
	}


	private void displayDesiredPaceOrSpeed() {
		if (mMaintain == PedometerSettings.M_PACE) {
			//mDesiredPaceView.setText("" + (int) mDesiredPaceOrSpeed);
		} else {
			//mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
		}
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "[ACTIVITY] onPause");
		if (mIsRunning) {
		}
		if (mQuitting) {
			//mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
		} else {
			//mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
		}
		
		super.onPause();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		//savePaceSetting();
		Log.i("INSIDEONPAUSE", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

		//Dheera: Added to resolve the memory leak issue
		if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
        }
		
		
	}
	
	class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	        
	    	
	  	HttpClient httpclient = new DefaultHttpClient();
	    	//HttpClient httpclient = HttpClientBuilder.create().build(); 
	        HttpResponse response;
	        StatusLine statusLine ;
	        String responseString = null;
	        /*   try {
	        	
	            response = httpclient.execute(new HttpGet(uri[0]));
	             statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                responseString = out.toString();
	                Log.i("IOTPART", "Status Code is OK...responsestring="+responseString);
	                out.close();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                Log.i("IOTPARTEXCEPTON", "Before throwing exception");
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	        	 Log.i("IOTPARTEXCEPTON", "1 exception is "+e.getMessage());
	            //TODO Handle problems..
	        } catch (IOException e) {
	        	 Log.i("IOTPARTEXCEPTON", "2 exception is "+e.getMessage());
	            //TODO Handle problems..
	        }
	        
	        JSONObject subscriber_payload = new JSONObject();
	        try {
	        	subscriber_payload.put("userID", "KidSense2");
	        	
	        	JSONObject channel1 = new JSONObject();
	        	channel1.put("type", "URL_Callback");
	        	channel1.put("data", "http://callbackURL.net/");
	        	
	        	JSONArray jsonArray = new JSONArray();
	        	jsonArray.put(channel1);
	        	subscriber_payload.put("channels", jsonArray);
	        	
	        	Log.i("IOT SUBSRIPTIONPAYLOAD", "json value is "+subscriber_payload.toString());
	        	
	        	
	        	 //url with the post data
	        	 HttpPost httpost = new HttpPost("http://russet.ischool.berkeley.edu:8080/users");
	        	 //passes the results to a string builder/entity
	        	 StringEntity se = new StringEntity(subscriber_payload.toString());
	        	
	        	  //sets the post request as the resulting string
	        	    httpost.setEntity(se);
	        	  //sets a request header so the page receving the request
	        	    //will know what to do with it
	        	    httpost.setHeader("Content-Type", "application/json");
	        	    Log.i("IOT DOINBACKGROUND", "Before execute...httpclient= "+httpclient);
	        	  //Handles what is returned from the page 
	        	    ResponseHandler responseHandler = new BasicResponseHandler();
	        	  
	        	    //UNCOMMENT THE FOLLOWING IF YOU WANT TO PUBLISH YOUR SERVICE AGAIN
	        	    //httpclient.execute(httpost, responseHandler);
	        	    
	        	    JSONObject new_subscription_payload = new JSONObject();
	        	    String idForURL="JohnnyWalker";
	        	    new_subscription_payload.put("subscriptionID", "KidSense_"+idForURL);
	        	    new_subscription_payload.put("userID", "KidSense");
	    	        	
	    	        	JSONObject obj3 = new JSONObject();
	    	        	JSONObject obj2 = new JSONObject();
	    	        	
	    	        	
	    	        	JSONArray jsonArray1 = new JSONArray();
	    	        	 jsonArray1.put("person");
	    	        	 obj2.put("$in",jsonArray1);
	    	        	 obj3.put("actor.objectType", obj2);
	    	        	 new_subscription_payload.put("ASTemplate",obj3);
	    	        	 
	    	        	 
	    	        	 //GET SUBSCRIPTIONS
	    	        	 try {
	    	        		 String uri_new = "http://russet.ischool.berkeley.edu:8080/users/"+"KidSense"+"/subscriptions";
	    	 	            response = httpclient.execute(new HttpGet(uri_new));
	    	 	            statusLine = response.getStatusLine();
	    	 	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	    	 	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	 	                response.getEntity().writeTo(out);
	    	 	                responseString = out.toString();
	    	 	                Log.i("IOTPARTFORSUBSCRIP", "Subscription Status Code is OK...responsestring="+responseString);
	    	 	                out.close();
	    	 	            } else{
	    	 	                //Closes the connection.
	    	 	                response.getEntity().getContent().close();
	    	 	                Log.i("IOTPARTEXCEPTONFORSUB", "Before throwing exception");
	    	 	                throw new IOException(statusLine.getReasonPhrase());
	    	 	            }
	    	 	        } catch (ClientProtocolException e) {
	    	 	        	 Log.i("IOTPARTEXCEPTONFORSUB", "1 exception is "+e.getMessage());
	    	 	            //TODO Handle problems..
	    	 	        } catch (IOException e) {
	    	 	        	 Log.i("IOTPARTEXCEPTONFORSUB", "2 exception is "+e.getMessage());
	    	 	            //TODO Handle problems..
	    	 	        }
	    	 	        
	    	        	
	    	        	
	    	        	Log.i("IOT new_subscription_payload", "json value is "+new_subscription_payload.toString());
	    	       
	    	        //POST THE ABOVE JSON
	    	        	 //url with the post data
	    	        	
	   	        	 HttpPost httpost2 = new HttpPost("http://russet.ischool.berkeley.edu:8080/users/"+idForURL+"/subscriptions");
	   	        	 //passes the results to a string builder/entity
	   	        	 StringEntity se2 = new StringEntity(new_subscription_payload.toString());
	   	        	
	   	        	  //sets the post request as the resulting string
	   	        	    httpost2.setEntity(se2);
	   	        	  //sets a request header so the page receving the request
	   	        	    //will know what to do with it
	   	        	    httpost2.setHeader("Content-Type", "application/json");
	   	        	    Log.i("IOT DOINBACKGROUND", "Before execute...httpclient= "+httpclient);
	   	        	  //Handles what is returned from the page 
	   	        	    ResponseHandler responseHandler2 = new BasicResponseHandler();
	   	        	  
	   	        	    //UNCOMMENT THE FOLLOWING IF YOU WANT TO PUBLISH YOUR SERVICE AGAIN
	   	        	    httpclient.execute(httpost2, responseHandler2);
	   	        	    
	        	    */
	        	    
	        	//POSTING STEPCOUNT
	   	        	    
	   	        	 JSONObject add_payload = new JSONObject();
	   	        	
	   	        	
	   	        	 JSONObject add_payload1 = new JSONObject();
	   	        	try {
						add_payload1.put("displayName","KidSense2");
					 
	   	        	add_payload1.put("id","http://example.org/AndroidPhone");
	   	        	add_payload1.put("objectType", "device");
	   	        	add_payload.put("actor", add_payload1);
	   	        	add_payload.put("verb", "Add");
	   	        	add_payload.put("status", "completed");
	   	        	
		   	         JSONObject add_payload3 = new JSONObject();
		   	         add_payload3.put("content",Integer.toString(state.getInt(childStepCountKey, 0)));
		   	        	
		   	         add_payload.put("object", add_payload3);
		   	   
//	   	        	 { "actor": { 
//	   	        		"displayName": "KidSense", 
//	   	        		"id": "http://example.org/AndroidPhone",
//	   	        		 "objectType": "device" },
//	   	        		 "verb": "Add",
//	   	        		"status": "completed",
//	   	        		"object": { 
//	   	        		"content": “20”,
//	   	        		} }}
	   	        	    
		   	         
			   	      HttpPost httpost3 = new HttpPost("http://russet.ischool.berkeley.edu:8080/activities");
		   	        	 //passes the results to a string builder/entity
	   	        	 StringEntity se3 = new StringEntity(add_payload.toString());
	   	        	Log.i("IOT ...pauload is", add_payload.toString());
	   	        	  //sets the post request as the resulting string
		   	        	    httpost3.setEntity(se3);
	   	        	  //sets a request header so the page receving the request
		   	        	    //will know what to do with it
	   	        	    httpost3.setHeader("Content-Type", "application/stream+json");
	  	        	    Log.i("IOT ADD DOINBACKGROUND", "Before execute...httpclient= "+httpclient);
	   	        	  //Handles what is returned from the page 
	   	        	    ResponseHandler responseHandler3 = new BasicResponseHandler();
	        	  
	   	        	    //UNCOMMENT THE FOLLOWING IF YOU WANT TO PUBLISH YOUR SERVICE AGAIN
	 	        	    httpclient.execute(httpost3, responseHandler3);
	   	        	    
	        	Log.i("IOT DOINBACKGROUND", "After write ");
	    }
	        	catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        
	        
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        Log.i("IOT OnPostExecute", "result is "+result);
	        //Do anything with response..
	        
	        
	    }
	}
	

	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] onStop");
		super.onStop();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		Log.i("INSIDEONSTOP", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

	}

	protected void onDestroy() {
		Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
		
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		
		Log.i("INSIDEONDESTROYOFPEDOMETER", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);
	}

	protected void onRestart() {
		Log.i(TAG, "[ACTIVITY] onRestart");
		super.onDestroy();
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		
		Log.i("INSIDEONRESTART", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

		
	}

	private void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
		if (mService != null) {
			if (mMaintain == PedometerSettings.M_PACE) {
				mService.setDesiredPace((int) desiredPaceOrSpeed);
			} else if (mMaintain == PedometerSettings.M_SPEED) {
				mService.setDesiredSpeed(desiredPaceOrSpeed);
			}
		}
	}

	private void savePaceSetting() {
	//	mPedometerSettings.savePaceOrSpeedSetting(mMaintain,mDesiredPaceOrSpeed);
	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(Pedometer.this, StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(Pedometer.this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		stateEditor.putInt("parentStepCountKey", mStepValue);
		stateEditor.putInt("parentCoinsKey", parentcoinsValue);
		stateEditor.putInt("childStepCountKey", childstepcount);
		stateEditor.putInt("childCoinsKey", childcoinValue);
		stateEditor.commit();
		unbindService(mConnection);
		
		
		Log.i("INSIDEunbind", "VALUES ==> "+mStepValue+" "+parentcoinsValue+" "+childstepcount+" "+childcoinValue);

	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(Pedometer.this, StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		if (mService != null && mIsRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			mChildStepCountView.setText("0");
			mChildCoinView.setText("0");
			mParentCoinView.setText("0");;
			//mPaceValueView.setText("0");
			//mDistanceValueView.setText("0");
			//mSpeedValueView.setText("0");
			//mCaloriesValueView.setText("0");
			
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				
				stateEditor.putInt("parentStepCountKey",0);
				stateEditor.putInt("parentCoinsKey",0);
				stateEditor.putInt("childStepCountKey",0);
				stateEditor.putInt("childCoinsKey",0);
				stateEditor.commit();
			}
		}
	}

	private static final int MENU_SETTINGS = 8;
	private static final int MENU_QUIT = 9;

	private static final int MENU_PAUSE = 1;
	private static final int MENU_RESUME = 2;
	private static final int MENU_RESET = 3;

	/* Creates the menu items */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		if (mIsRunning) {
			menu.add(0, MENU_PAUSE, 0, R.string.pause)
					.setIcon(android.R.drawable.ic_media_pause)
					.setShortcut('1', 'p');
		} else {
			menu.add(0, MENU_RESUME, 0, R.string.resume)
					.setIcon(android.R.drawable.ic_media_play)
					.setShortcut('1', 'p');
		}
		menu.add(0, MENU_RESET, 0, R.string.reset)
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setShortcut('2', 'r');
		menu.add(0, MENU_SETTINGS, 0, R.string.settings)
				.setIcon(android.R.drawable.ic_menu_preferences)
				.setShortcut('8', 's')
				.setIntent(new Intent(this, Settings.class));
		menu.add(0, MENU_QUIT, 0, R.string.quit)
				.setIcon(android.R.drawable.ic_lock_power_off)
				.setShortcut('9', 'q');
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PAUSE:
			unbindStepService();
			stopStepService();
			return true;
		case MENU_RESUME:
			startStepService();
			bindStepService();
			return true;
		case MENU_RESET:
			resetValues(true);
			return true;
		case MENU_QUIT:
			resetValues(false);
			//unbindStepService();
			stopStepService();
			mQuitting = true;
			finish();
			return true;
		}
		return false;
	}

	// TODO: unite all into 1 type of message
	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			
		
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
			
			
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void distanceChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), 0));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value), 0));
		}
	};

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = (int) msg.arg1;
		
				
				parentcoinsValue=mStepValue/25;
				
				Log.i("HANDLER VALUES"," parentsteps="+mStepValue+ " parentcoins="+parentcoinsValue+" childteps="+state.getInt(childStepCountKey, 0)+" childcoins="+state.getInt(childCoinsKey, 0));
				
				//Adding stepcount and coin values to SharedPreferences
				stateEditor.putInt("parentStepCountKey", mStepValue);
				stateEditor.putInt("parentCoinsKey", parentcoinsValue);
				stateEditor.putInt("childStepCountKey", state.getInt(childStepCountKey, 0));
				stateEditor.putInt("childCoinsKey",state.getInt(childCoinsKey, 0));
				stateEditor.commit();
				
				if(mStepValue>=100 )
				{
					Toast.makeText(Pedometer.this, "Garfield completed TreasureTrail 1",Toast.LENGTH_SHORT).show();

				}
				if(state.getInt(childStepCountKey, 0)>=100 )
				{
					Toast.makeText(Pedometer.this, "Odie completed TreasureTrail 1",Toast.LENGTH_SHORT).show();

				}
				if(mStepValue==parentGoalValue/4)
				{
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue/2){
					
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue*0.75){
					
					coinsValue++;
				}
				else if (mStepValue==parentGoalValue){
					coinsValue++;
					 AlertDialog.Builder alert = new AlertDialog.Builder(Pedometer.this);
					    alert.setTitle("TreasureTrail in Italy is complete. Celebrate with snapshot!");
					    // alert.setMessage("Message");

					    alert.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int whichButton) {
					            //Your action here
					        }
					    });

					    alert.setNegativeButton("May Be Later",
					        new DialogInterface.OnClickListener() {
					            public void onClick(DialogInterface dialog, int whichButton) {
					            }
					        });

					    alert.show();
				}
				mStepValueView.setText(mStepValue+" steps");
				mParentCoinView.setText("Coins - "+state.getInt(parentCoinsKey, 0));
				mChildStepCountView.setText(state.getInt(childStepCountKey, 0)+" steps");
				mChildCoinView.setText("Coins - "+state.getInt(childCoinsKey, 0));
				
				
				
				
				break;
			case PACE_MSG:
				//mPaceValue = msg.arg1;
				//if (mPaceValue <= 0) {
					//mPaceValueView.setText("0");
				//} else {
					//mPaceValueView.setText("" + (int) mPaceValue);
				//}
				break;
			case DISTANCE_MSG:
				//mDistanceValue = ((int) msg.arg1) / 1000f;
				//if (mDistanceValue <= 0) {
					//mDistanceValueView.setText("0");
				//} else {
					//mDistanceValueView.setText(("" + (mDistanceValue + 0.000001f)).substring(0, 5));
				//}
				break;
			case SPEED_MSG:
				//mSpeedValue = ((int) msg.arg1) / 1000f;
				//if (mSpeedValue <= 0) {
					//mSpeedValueView.setText("0");
				//} else {
					//mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f)).substring(0, 4));
				//}
				break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				if (mCaloriesValue <= 0) {
					//mCaloriesValueView.setText("0");
				} else {
					//mCaloriesValueView.setText("" + (int) mCaloriesValue);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

}