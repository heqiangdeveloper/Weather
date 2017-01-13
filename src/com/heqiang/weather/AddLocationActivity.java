package com.heqiang.weather;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class AddLocationActivity extends Activity {
    @SuppressWarnings("unused")
	private EditText input;
    private ListView list;
    private ImageView back_img,clear_img;
    private ProgressBar progress;
    private String cityName,language,url;
    private String TAG = "mylogs";
    private UrlRequest urlRequest;
    private ResponseUtil responseUtil;
    private RequestQueue requestQueue;
    
    private ArrayList<Day> dayList;
    private List<City> tempCityList;
    private ArrayAdapter<City> adapter;
    private List<String> cityinfo_list;

    private CheckNetwork checkNetwork;
    
    private static final int GET_CITY_FAIL = 1;
    private static final int GET_CITY_SUCCESS = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int CLEAR_IMG_VISIBLE = 4;
    private static final int CLEAR_IMG_INVISIBLE = 5;
    private static final int UPDATE_LIST = 6;
    private static final int TO_MAINACTIVITY = 7;
    private static final int GET_CURRENTCONDITIONS_FAIL = 8;
    private static final int GET_LOCATION_POPUP_PROGRESS_DIALOG = 9;
    private static final int AUTO_LOCATION_POPUP_PROGRESS_DIALOG = 10;
    private static final int UNPOPUP_PROGRESS_DIALOG = 11;
    private static final int AUTO_FAIL = 12;
    private static final int AUTO_SUCCESS = 13;
    private static final int DOGETFORECASTSTHREAD = 14;
    //private static final int INSERTDB = 11;
    
    private Thread thread,thread_forecasts;
    private SharedPreferences preferences ;
    private Editor editor; 

    private String SELECTED_CITYKEY = "";
    private String mKey;
    private boolean isAddCity;
    private CurrentConditions current = null;
    private ProgressDialog dialog;
    private WeatherDB db;
    private Context mContext;
    private ImageButton auto_btn;
    
    private LocationManager mLocationManager;
    private Location mNetworkLocation,mGPSLocation,mLocation;
    private LocationProvider mLocationProvider;
    private boolean isNetworkProvideEnable = false;
    private boolean isGPSProvideEnable = false;
    private boolean isAutoLocateSuccess = false;
    private boolean isNetworkError = false;
    
    private long minTime = 1000;//10s
    private LocationListener mNetworkListener;
    private LocationListener mGPSListener;
    
    private int retryTime = 10;
    private int requestTimeOut = 30 * 1000;
    private int foreSize = 4;
    Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(!isCurrentThreadInterrupted()){
				switch(msg.what){
				case GET_CITY_FAIL:
					if(cityinfo_list != null && cityinfo_list.size() != 0){
						cityinfo_list.clear();
						setListAdapter(cityinfo_list);					
					}
					showNetworkErrorToast(GET_CITY_FAIL);
					break;
				case GET_CITY_SUCCESS:				
					String cityName, Country, State;
					City city;
					cityinfo_list.clear();
					for(int i = 0; i < tempCityList.size(); i++){
						city = tempCityList.get(i);
						cityName = city.getCityName();
						Country = city.getCountry();
						State = city.getState();
						cityinfo_list.add(cityName + "," + Country + "," + State);
					}
					
					setListAdapter(cityinfo_list);
					break ;
				case NETWORK_ERROR:
					showNetworkErrorToast(NETWORK_ERROR);
					break;			
				case CLEAR_IMG_VISIBLE:
					clear_img.setVisibility(View.VISIBLE);
					break;
				case CLEAR_IMG_INVISIBLE:
					clear_img.setVisibility(View.INVISIBLE);
					break;
				case UPDATE_LIST:
					setListAdapter(cityinfo_list);
					break;
				case TO_MAINACTIVITY:
					//Intent i = new Intent(WeatherActivity.this, ForecastPage.class);	
					Intent i = new Intent(AddLocationActivity.this, MainActivity.class);
					//i.putExtra("searchCityKey", (Serializable)dayList); 
					i.putExtra("searchCityConditions", (Serializable)current);
					i.putParcelableArrayListExtra("searchCityForecasts", dayList);
					startActivity(i);
					AddLocationActivity.this.finish();
					break;
				case GET_CURRENTCONDITIONS_FAIL:
					showNetworkErrorToast(GET_CURRENTCONDITIONS_FAIL);	
					break;
				case GET_LOCATION_POPUP_PROGRESS_DIALOG:
					popupProgressDialogOrNot(GET_LOCATION_POPUP_PROGRESS_DIALOG);
					break;
				case AUTO_LOCATION_POPUP_PROGRESS_DIALOG:
					popupProgressDialogOrNot(AUTO_LOCATION_POPUP_PROGRESS_DIALOG);
					break;
				case UNPOPUP_PROGRESS_DIALOG:
					popupProgressDialogOrNot(UNPOPUP_PROGRESS_DIALOG);
					break;
				/*case INSERTDB:					
					db.insert(current);
					break;*/
				case AUTO_FAIL:
					showNetworkErrorToast(AUTO_FAIL);
				case AUTO_SUCCESS:
					showNetworkErrorToast(AUTO_SUCCESS);
					break;
				case DOGETFORECASTSTHREAD:
					doGetForecastsThread();
					break;
				default:
					break;
				}			
				
			}
			
			super.handleMessage(msg);
		}
    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main); 
        mContext = AddLocationActivity.this;
        
        preferences = getSharedPreferences("currentcity", MODE_PRIVATE);
        editor = preferences.edit();  
        db = new WeatherDB(AddLocationActivity.this);        
             
    	findView();
        setListener();
        
        requestQueue = Volley.newRequestQueue(this);
     
        checkNetwork = new CheckNetwork(AddLocationActivity.this);
        if(!checkNetwork.isNetworkAvailable()){
        	showNetworkErrorToast(NETWORK_ERROR);
        }
        
        cityinfo_list = new ArrayList<String>();
        responseUtil = new ResponseUtil();
   
        dialog = new ProgressDialog(AddLocationActivity.this);   
        
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mNetworkListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {				
				
			}
			
			public void onProviderEnabled(String provider) {
				
			}
			
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location location) {
				if(null != location){
					Log.i(TAG, "networkLocation: Longitude = " + location.getLongitude() +
							",Latitude = " + location.getLatitude());
					mNetworkLocation = location;
					isAutoLocateSuccess = true;
				}else{
					Log.i(TAG, "NetworkLocation by locationChanged is: null... ");
				}
				
			}
		};
		
		mGPSListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderEnabled(String provider) {
				
			}
			
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location location) {
				if(null != location){
					Log.i(TAG, "GPSLocation: Longitude = " + location.getLongitude() +
							",Latitude = " + location.getLatitude());
					mGPSLocation = location;
					isAutoLocateSuccess = true;
				}else{
					Log.i(TAG, "GPSLocation by locationChanged is: null... ");
				}
				
			}
		};
    }

	public void showNetworkErrorToast(int flag){
    	String info = "";
    	switch (flag) {
	    	case GET_CITY_FAIL:
	    		info = getResources().getString(R.string.no_city);
	    		break;
	    	case AUTO_FAIL:
	    		info = getResources().getString(R.string.auto_fail);
	    		break;
	    	case AUTO_SUCCESS:
	    		info = getResources().getString(R.string.auto_success);
	    		break;
	    	case NETWORK_ERROR:
	    		info = getResources().getString(R.string.network_error);
	    		break;
	    	case GET_CURRENTCONDITIONS_FAIL:
	    		info = getResources().getString(R.string.no_currentconditions);
	    		break;
	    	default:
	    		break;
    	}
    	Toast.makeText(AddLocationActivity.this, info, Toast.LENGTH_SHORT).show();
    }
    
    public void popupProgressDialogOrNot(int flag){
    	if(flag == UNPOPUP_PROGRESS_DIALOG){  
    		if(dialog.isShowing()){
    			dialog.dismiss();    			
    		}
    	}else if(flag == GET_LOCATION_POPUP_PROGRESS_DIALOG){    		
    		dialog.setMessage(getResources().getString(R.string.getting));   
    		dialog.setProgressStyle(dialog.STYLE_SPINNER);
    		dialog.setIndeterminate(false);
    		dialog.setCancelable(false);
    		dialog.show();
    	}else if(flag == AUTO_LOCATION_POPUP_PROGRESS_DIALOG){    		
    		dialog.setMessage(getResources().getString(R.string.autoing)); 
    		dialog.setProgressStyle(dialog.STYLE_SPINNER);
    		dialog.setIndeterminate(false);
    		dialog.setCancelable(false);
    		dialog.show();
    	}
    }
    public void setListAdapter(List<String> data){
    	int searchCityLen = cityName.trim().length();
    	String [] cityNames = new String [11];
    	String [] countryNames = new String [11];
    	for(int i = 0; i< data.size(); i++){
    		cityNames[i] = data.get(i).substring(0,searchCityLen);
    		countryNames[i] = data.get(i).substring(searchCityLen);
    	}
    	
    	List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
    	for(int i = 0; i< data.size(); i++){
    		Map<String, Object> listitem = new HashMap<String, Object>();
    		listitem.put("cityName", cityNames[i]);
    		listitem.put("countryName", countryNames[i]);
    		listItems.add(listitem);
    	}
    	
    	SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems,
    			R.layout.list_layout,
    			new String[] {"cityName","countryName"},
    			new int[] {R.id.cityinfo_txt1, R.id.cityinfo_txt2});
    	list.setAdapter(simpleAdapter);
    }
    
    public void findView(){
    	input = (EditText) findViewById(R.id.input);
    	list = (ListView) findViewById(R.id.list);
    	progress = (ProgressBar) findViewById(R.id.progress);
    	//progress.setVisibility(View.GONE);
    	back_img = (ImageView) findViewById(R.id.back_img);
    	clear_img = (ImageView) findViewById(R.id.clear_img);    	  	
    	auto_btn = (ImageButton)findViewById(R.id.auto);
    }
    
    public void setListener(){
    	input.addTextChangedListener(new mTextWatcher());
    	back_img.setOnClickListener(new mClick());
    	clear_img.setOnClickListener(new mClick());
    	clear_img.setVisibility(View.INVISIBLE);
    	list.setOnItemClickListener(new mOnItemClick());
    	auto_btn.setOnClickListener(new mClick());
    }

    class mClick implements OnClickListener{

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_img:
				back();
				break;
			case R.id.clear_img:
				input.setText("");
				if(cityinfo_list != null && cityinfo_list.size() != 0){
					cityinfo_list.clear();
					//sendMsg(UPDATE_LIST);	
					setListAdapter(cityinfo_list);
				}
				break;
			case R.id.auto:
				autoLocate();			

			default:
				break;
			}
			
		}
    	
    }
    
    public void autoLocate(){    	
    	checkNetwork = new CheckNetwork(AddLocationActivity.this);
		if(!checkNetwork.isNetworkAvailable()){
			sendMsg(NETWORK_ERROR);
		}else {										
			thread = new Thread(){
				
				@Override
				public void run() {	
					Log.i(TAG, "autolocating...");
					Looper.prepare();
					sendMsg(AUTO_LOCATION_POPUP_PROGRESS_DIALOG);
					autoLocateCity();
				}
			};
			thread.start();				
		}
    }
    
    public void autoLocateCity(){
    	responseUtil = new ResponseUtil();
    	PackageManager pm = getApplicationContext().getPackageManager();
        isNetworkProvideEnable = (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK));
        isGPSProvideEnable = (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
        Log.i(TAG, "isNetworkProvideEnable: " + isNetworkProvideEnable);
        Log.i(TAG, "isGPSProvideEnable: " + isGPSProvideEnable);
        editor.putBoolean("isNetworkProvideEnable", false);
        editor.putBoolean("isGPSProvideEnable", false);
        editor.commit();
        
        if(isNetworkProvideEnable){
        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, 1, mNetworkListener);
        }
        
        if(isGPSProvideEnable){
        	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, 1, mGPSListener);
        }
        isAutoLocateSuccess = false;         
        Log.i(TAG, "first check isAutoLocateSuccess: " + isAutoLocateSuccess);
        int i;
        for (i = 0; i < retryTime; i++) {            
            if (!isAutoLocateSuccess) {
            	checkNetwork = new CheckNetwork(AddLocationActivity.this);
                if (!checkNetwork.isNetworkAvailable()) {
                    Log.i(TAG, "isNetwrokConnected is false");
                    isNetworkError = true;
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                isAutoLocateSuccess = true;
                break;
            }
        }
        Log.i(TAG,"retry time is:" + i);
        Log.i(TAG, "after retryTime isAutoLocateSuccess is: " + isAutoLocateSuccess);
                
        if(isAutoLocateSuccess){
        	if(isBetterLocation(mGPSLocation,mNetworkLocation)){
        		 mLocation = mGPSLocation;     
        		 Log.i(TAG, "betterLocation is:" + "GPSLocation");
        	}else{
        		 mLocation = mNetworkLocation;
        		 Log.i(TAG, "betterLocation is:" + "NetworkLocation");
        	}
        	Log.i(TAG, "mLocation is:" + mLocation);
        	/*if(null != mLocation){
        		JSONArray geoInfoJsonArray = getLocationData(mLocation);
        		Log.i(TAG, "geoInfoJsonArray is:" + geoInfoJsonArray);
        		if(null != geoInfoJsonArray && geoInfoJsonArray.length() != 0){
        			City mCity = responseUtil.getCityInfoFromGeoJsonArray(geoInfoJsonArray);
        			Log.i(TAG, "geoCityKey is:" + mCity.getLocationKey());
        			if(null != mCity){
        				sendCurrentConditionsFindRequest(mCity.getCityName(), mCity.getLocationKey(), null, null, "zh-cn");        			
        			}      			
        		}else{
        			sendMsg(AUTO_FAIL);
        		}
        	}else{
        		sendMsg(AUTO_FAIL);
        	}*/
        	sendAutoLocateRequest(mLocation);
        }
        
        if(!isAutoLocateSuccess){
        	mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	if(null != mLocation){
        		isAutoLocateSuccess = true;
        		Log.i(TAG,"getLastKnownLocation is:" + mLocation);
        		sendAutoLocateRequest(mLocation);
        	}else{
        		Log.i(TAG,"getLastKnownLocation is: null");
        	}
        }else if(isNetworkError){
        	//sendMsg(UNPOPUP_PROGRESS_DIALOG);
			sendMsg(NETWORK_ERROR);
		}else{
			//mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			sendMsg(AUTO_FAIL);
		}
        mLocationManager.removeUpdates(mGPSListener);
        mLocationManager.removeUpdates(mNetworkListener);
        
        sendMsg(UNPOPUP_PROGRESS_DIALOG);
    }
    
    private void sendAutoLocateRequest(Location location){
    	if(null != location){
    		JSONObject geoInfoJsonObject = getLocationData(location);
    		Log.i(TAG, "geoInfoJsonObject is:" + geoInfoJsonObject);
    		if(null != geoInfoJsonObject && geoInfoJsonObject.length() != 0){
    			City mCity = responseUtil.getCityInfoFromGeoJsonArray(geoInfoJsonObject);
    			Log.i(TAG, "geoCityKey is:" + mCity.getLocationKey());
    			if(null != mCity){
    				sendCurrentConditionsFindRequest(mCity.getCityName(), mCity.getLocationKey(), null, null, "zh-cn", AUTO_FAIL);        			
    			}      			
    		}else{
    			sendMsg(AUTO_FAIL);
    		}
    	}else{
    		sendMsg(AUTO_FAIL);
    	}
    }
    
    private boolean isBetterLocation(Location locationA, Location locationB) {
        if (locationA == null) {
            return false;
        }
        if (locationB == null) {
            return true;
        }
        /*if (locationA.getElapsedRealtimeNanos() > locationB.getElapsedRealtimeNanos() + 11 * 1000000000)) {
            return true;
        } else if (locationB.getElapsedRealtimeNanos() > (locationA.getElapsedRealtimeNanos() + 11 * 1000000000)) {
            return false;
        }*/

        if (!locationA.hasAccuracy()) {
            return false;
        }
        if (!locationB.hasAccuracy()) {
            return true;
        }
        return locationA.getAccuracy() < locationB.getAccuracy();
    }
     
	private JSONObject getLocationData(Location location){
    	if(null != location){
    		double latitude = location.getLatitude();
    		double longitude = location.getLongitude();
    		String geoLocation = latitude + "," + longitude;
    		
    		urlRequest = new UrlRequest();
    		String urlString = "";
    		urlString = urlRequest.findCityByGeoLocation(geoLocation, "zh-cn", true);
    		Log.i(TAG, "findCityByGeoLocationUrl: " + urlString);
        	
    		JSONObject cityInfojsonObject = null;
			try {
				cityInfojsonObject = getJSONFromLocationRequest(urlString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cityInfojsonObject;				
    	}else {
			return null;
		}
    }
    
    private JSONObject getJSONFromLocationRequest(String path) throws Exception{
    	URL url = new URL(path);
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	connection.setConnectTimeout(requestTimeOut);
    	connection.setRequestMethod("GET");
    	if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
    		Log.i(TAG, "connection is OK!");
    		InputStream inStream = connection.getInputStream();    	
    		
    		if(null != inStream){
    			int len = 0;
    			byte [] b = new byte[2048];
    			StringBuffer sb = new StringBuffer();
    			JSONObject jObject = null;
    			try {    				
    				while( (len = inStream.read(b)) != -1){
    					sb.append(new String(b,0,len,"utf-8"));
    				}    		
    				Log.i(TAG, "sb is: " + sb.toString());
    				jObject = new JSONObject(sb.toString());
    				return jObject;    				
    			} catch (IOException e) {
    				Log.i(TAG, "get InputStream error!");
    			}finally{
    				inStream.close();
    				connection.disconnect();			
    			}    	
    		}
    	}
    	return null;
    }
    
    class mOnItemClick implements OnItemClickListener{

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			SELECTED_CITYKEY = getSelectedCityKey(tempCityList.get(arg2));
			final String SELECTED_CITYNAME = getSelectedCityName(tempCityList.get(arg2));	
			checkNetwork = new CheckNetwork(AddLocationActivity.this);
			if(!checkNetwork.isNetworkAvailable()){
				//sendMsg(UNPOPUP_PROGRESS_DIALOG);
				sendMsg(NETWORK_ERROR);
			}else {					
				thread = new Thread(){
					
					@Override
					public void run() {			
						Looper.prepare();			
						sendMsg(GET_LOCATION_POPUP_PROGRESS_DIALOG);
						sendCurrentConditionsFindRequest(SELECTED_CITYNAME, SELECTED_CITYKEY, null, null, "zh-cn", GET_CURRENTCONDITIONS_FAIL);
						//sendMsg(DOGETFORECASTSTHREAD);
					}
					
				};
				thread.start();
			}
		}
    	
    }
    
    public void doGetForecastsThread(){
		thread_forecasts = new Thread(){

			@Override
			public void run() {
				Log.i(TAG, "start to getForecast...");
				Log.i(TAG, "SELECTED_CITYKEY is: " + SELECTED_CITYKEY);
				sendForecastDailyWeatherFindRequest(SELECTED_CITYKEY, null, null, "zh-cn");									
				super.run();
			}
		};
		thread_forecasts.start();
    }
    
    public void sendForecastDailyWeatherFindRequest(final String locationKey, String lantitude, String longitude, String language){
		urlRequest = new UrlRequest();
		String forecastUrl = urlRequest.forecastDailyWeatherUrl(locationKey, lantitude, longitude, language);
		Log.i(TAG,"forecastUrl is:" + forecastUrl);
		
		responseUtil = new ResponseUtil();		
		dayList = new ArrayList<Day>();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(forecastUrl, null,
				new Response.Listener<JSONObject>() {

					public void onResponse(JSONObject response) {
						Log.i(TAG,"get5DayForecastData is:" + response);
                    	dayList = responseUtil.getForecastFromJSON(AddLocationActivity.this, response);	
                    	Log.i(TAG,"daylist is:" + dayList);
                    	for(int i = 0; i < foreSize; i++){
                    		(dayList.get(i)).setKey(locationKey);
            				(dayList.get(i)).setDayNum(String.valueOf(i + 2));
            			}
                    	Log.i(TAG,"dayList is:" + dayList);
                    	//sendMsg(UNPOPUP_PROGRESS_DIALOG);
						if(null == dayList || dayList.size() == 0){
							sendMsg(UNPOPUP_PROGRESS_DIALOG);
							sendMsg(GET_CURRENTCONDITIONS_FAIL);	
						}else {		
							Log.i(TAG,"to MainActivity...");
							sendMsg(UNPOPUP_PROGRESS_DIALOG);
							sendMsg(TO_MAINACTIVITY);							
						}						
					}
				}, 
				new Response.ErrorListener() {

					public void onErrorResponse(VolleyError error) {
						sendMsg(UNPOPUP_PROGRESS_DIALOG);
						sendMsg(GET_CURRENTCONDITIONS_FAIL);						
					}
				});
		jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);	
    }
    
    public void sendCurrentConditionsFindRequest(final String selectedCityName, final String selectedCityKey,
			String lantitude, String longitude, String language, final int failString){
		urlRequest = new UrlRequest();
		String cityKeyUrl = urlRequest.currentWeatherUrl(selectedCityKey, lantitude, longitude, language);
		responseUtil = new ResponseUtil();		
		current = new CurrentConditions();
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(cityKeyUrl, 
				new Response.Listener<JSONArray>() {

					public void onResponse(JSONArray response) {
						//editor.putString("currentCity", "" + response);
                    	//editor.commit();
						Log.i(TAG, "getCurrentConditions is:" + response);
						
						current = responseUtil.getCurrentConditionsFromJSON(mContext, response);
						//sendMsg(UNPOPUP_PROGRESS_DIALOG);
						if(null == current){
							sendMsg(UNPOPUP_PROGRESS_DIALOG);
							sendMsg(failString);	
						}else {
							current.setKey(selectedCityKey);
							current.setName(selectedCityName);
							SELECTED_CITYKEY = selectedCityKey;
							sendMsg(DOGETFORECASTSTHREAD);	
							//sendMsg(AUTO_SUCCESS);							
							//sendMsg(TO_MAINACTIVITY);	
						}						
					}
			
		}, new Response.ErrorListener() {

			public void onErrorResponse(VolleyError error) {
				sendMsg(UNPOPUP_PROGRESS_DIALOG);
				sendMsg(failString);				
			}
		});

		jsonArrayRequest.setShouldCache(false);
        requestQueue.add(jsonArrayRequest);	      
    }
    
    public String getSelectedCityKey(City city){
    	return city.getLocationKey();
    }
    
    public String getSelectedCityName(City city){
    	return city.getCityName();
    }
    
      
    private void sendMsg(int flag){
    	Message msg = new Message();
    	msg.what = flag;
    	mHandler.sendMessage(msg);
    }
    
    class mTextWatcher implements TextWatcher{


		public void afterTextChanged(Editable s) {
			
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			thread = new Thread(){

				@Override
				public void run() {
					cityName = input.getText().toString().trim();
					//language = getSystemService(Context.)
					if (!"".equals(cityName) && !isCurrentThreadInterrupted()) {
						sendMsg(CLEAR_IMG_VISIBLE);		
						checkNetwork = new CheckNetwork(AddLocationActivity.this);
						if (!checkNetwork.isNetworkAvailable()) {
							sendMsg(NETWORK_ERROR);							 
						}else {
							sendCityFindRequest(cityName,"");			
						}
					}else {
						thread.interrupt();
						sendMsg(CLEAR_IMG_INVISIBLE);
						if(cityinfo_list != null && cityinfo_list.size() != 0 ){
							cityinfo_list.clear();
							sendMsg(UPDATE_LIST);						
						}
					}
					
					super.run();
				}
				
			};
			thread.start();
			
		}
    	
    }

    public void sendCityFindRequest(final String cityName, final String reqId) {
    	urlRequest = new UrlRequest();
    	try {
			url = urlRequest.findCityByName(URLEncoder.encode(cityName, "utf-8"), "zh-cn", true);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//editor.putString("findCityUl", "" + url);
    	//editor.commit();
    	
    	JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray jsonArray) {
                        //Log.i(TAG, "city find request respose=" + jsonArray.toString());
                    	//editor.putString("citylist", "" + jsonArray);
                    	//editor.commit();
                        
                        tempCityList = responseUtil.getCityListFromJSON(jsonArray);
                        Log.i(TAG, "citylist=" + tempCityList.toString());
                        
                        if (tempCityList == null || tempCityList.size() == 0) {
                        	sendMsg(GET_CITY_FAIL);                    
                        } else {
                            sendMsg(GET_CITY_SUCCESS);
                        }
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        /*Log.e(TAG, "city find request error::" + volleyError);
                        Intent it = new Intent("android.intent.action.WEATHER_BROADCAST");
                        it.putExtra("connect_timeout", true);
                        sendBroadcast(it);*/
                    	
                    	sendMsg(GET_CITY_FAIL);
                    }
                });
        jsonArrayRequest.setShouldCache(false);
        requestQueue.add(jsonArrayRequest);
    }
    
    public boolean isCurrentThreadInterrupted(){
    	return thread.currentThread().isInterrupted();
    }

	@Override
	public void onBackPressed() {
		back();
		super.onBackPressed();
	}
	
	public void back(){
		mKey = preferences.getString("currentkey", "unknown");
		if(!"unknown".equals(mKey)){
			Intent i = new Intent(AddLocationActivity.this, MainActivity.class);
			//i.putExtra("searchCityConditions", (Serializable)null);
			startActivity(i);
			AddLocationActivity.this.finish();
		}else {
			AddLocationActivity.this.finish();
		}
	}
    
    
}