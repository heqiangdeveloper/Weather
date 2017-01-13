package com.heqiang.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity implements OnClickListener {
	private ViewPager viewPager;
	private ArrayList<View> viewLists;
	private MyPagerAdapter adapter;
	private View[] views;
	private CurrentConditions[] ccs;
	private ArrayList<Day> foreDayLists, feedbackDayLists;
	
	//private CurrentConditions cc;
	private int temperature;
	private String date, weekDay, time, weathertext;
	private int realfeel;
	private int humidity;
	private float windspeed;
	private float visibility;
	private int current_hightemp;
	private int current_lowtemp;
	private String direction;
	private String cityName, cityKey;
	private Day day;
	
	private TextView realfeelTv,humidityTv,windspeedTv,visibilityTv,moreTv;
	private TextView realfeelUnitTv,windspeedUnitTv,visibilityUnitTv;
	private LinearLayout[] linearLayout_day;
	private TextView[] Tv_week;
	private ImageView[] Iv_day;
	private MarqueeTextView[] marq_weathertxt;
	private TextView[] Tv_high,Tv_low;

	private TextView dateTv,dayTv,timeTv,temperatureLabel, weathertextLabel;
	private MarqueeTextView cityLabel;
	
	private PopupWindow popupWindow;
	private ImageView menu;
	private TextView menuAddLocation,menuDeleteLocation,menuRefresh,menuTempUnit,menuDistUnit;
	private SharedPreferences settings_preferences,currentCity_preference;
	private Editor editor, currentCity_editor;
	
	private String CF, KMMI, realfeelUnitLabel, windspeedUnitLabel, visibleUnitLabel, mCurrentKey;
	private CheckNetwork checkNetwork;
	private static final int POPUP_PROGRESS_DIALOG = 1;
    private static final int UNPOPUP_PROGRESS_DIALOG = 2;
    private static final int NETWORK_ERROR = 3;  
    private static final int UPDATE_CURRENTCONDITIONS_FAIL = 4; 
    private static final int UPDATE_CURRENTCONDITIONS_SUCCESS = 5;    
    private static final int UPDATEDB = 6;
    private static final int UPDATE_DATA = 7;
    private static final int TOADDLOCATIONACTIVITY = 8;
    private static final int OVERMAXCITYWARNING = 9;
    private static final int DOGETFORECASTSTHREAD = 10;
    
    private UrlRequest urlRequest;
    private ResponseUtil responseUtil;
    private RequestQueue requestQueue;
    private ProgressDialog dialog;
    private WeatherDB db;
    
    private VideoView mVideoView;
    private ImageView mImageView;
    private Integer iconNum, imgNum;
    private boolean isDayTime;
    private int position, citySize;
    private Uri backgroundIconUri;
    
    private LinearLayout popup_menu;
    private Context mContext;
    
    private int maxCitySize = 15;
    private boolean isUsePic = true;
    private String mobileLink;
    private LinearLayout rootContainer;
    private static int current_progress_icon = R.drawable.ic_the_current_progress;
    private static int non_current_progress_icon = R.drawable.ic_non_current_progress;

    private static final String CURRENT_CITY_CHANGED_BROADCAST = "com.heqiang.weather.MainActivity.CURRENTCITYCHANGEDBROADCAST";
    private static final String UNIT_CHANGED_BROADCAST = "com.heqiang.weather.MainActivity.UNITCHANGEDBROADCAST";
    
    private CurrentConditions updateConditions;
    private String TAG = "MainActivityLogs";
    
    private int ForeSize = 4;
    private String[] mobileLink_days, weekDays, foreweathertxts;
    private int[] dayImages;
    private int[] highs;
    private int[] lows;
    
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case POPUP_PROGRESS_DIALOG:
				popupProgressDialogOrNot(POPUP_PROGRESS_DIALOG);
				break;
			case UNPOPUP_PROGRESS_DIALOG:
				popupProgressDialogOrNot(UNPOPUP_PROGRESS_DIALOG);
				break;
			case NETWORK_ERROR:
				showToast(NETWORK_ERROR);
				break;
			case UPDATE_CURRENTCONDITIONS_FAIL:
				showToast(UPDATE_CURRENTCONDITIONS_FAIL);
				break;
			case UPDATE_CURRENTCONDITIONS_SUCCESS:
				showToast(UPDATE_CURRENTCONDITIONS_SUCCESS);
				break;
			case UPDATEDB:
				db.update(updateConditions);
				for (int i = 0; i < ForeSize; i++) {
					db.update_forecasts(feedbackDayLists.get(i));
				}
				break;
			case UPDATE_DATA:
				//getSettings();
				//getDataFromCC(current);				
				//initDataAndUnits();
				//setTexts();
				//InitForecastWeather();
				//initBackgroundIcon(iconNum);
				views = new View[maxCitySize];
				AddViews();
				break;
			case TOADDLOCATIONACTIVITY:
				Intent i = new Intent(mContext, AddLocationActivity.class);
				i.putExtra("addCity", true);
				startActivity(i);
				finish();		
				break;
			case OVERMAXCITYWARNING:
				showToast(OVERMAXCITYWARNING);
				break;
			case DOGETFORECASTSTHREAD:
				doGetForecastsThread();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.current_main);	
		
		mContext = MainActivity.this;
		db = new WeatherDB(mContext);		
		updateConditions = new CurrentConditions();
		
		currentCity_preference = getSharedPreferences("currentcity", MODE_PRIVATE);
        currentCity_editor = currentCity_preference.edit(); 
        settings_preferences = getSharedPreferences("settings", MODE_PRIVATE);
		editor = settings_preferences.edit();		
		
		CurrentConditions conditions = (CurrentConditions) getIntent().getSerializableExtra("searchCityConditions");
		ArrayList<Day> dayLists = getIntent().getParcelableArrayListExtra("searchCityForecasts");
		if(null != conditions && null != dayLists && dayLists.size() != 0){
			String searchCityKey = conditions.getKey();
			
			currentCity_editor.putString("currentkey", searchCityKey);
			currentCity_editor.commit();
			
			ArrayList<String> savedKeys = db.getSavedKeys();			
			if(null == savedKeys){
				db.insert(conditions);
				for (Day day : dayLists) {
					db.insert_forecasts(day);
				}
			}else {		
				int i;
				for(i = 0; i < savedKeys.size(); i++){
					if(searchCityKey.equals(savedKeys.get(i))){
						db.update(conditions);
						for (Day day : dayLists) {
							db.update_forecasts(day);
						}
						break;
					}
				}
				if(i >= savedKeys.size()){
					db.insert(conditions);
					for(Day day : dayLists) {
						db.insert_forecasts(day);
					}
				}
			}
		}
		   
	    mCurrentKey = currentCity_preference.getString("currentkey", "unknown");	
	    //String widgetLocationKey =  getIntent().getStringExtra("widgetLocationKey");
		/*if(!"".equals(widgetLocationKey) && null != widgetLocationKey){
			mCurrentKey = widgetLocationKey;
			currentCity_editor.putString("currentkey", mCurrentKey);
			currentCity_editor.commit();
			Toast.makeText(mContext, "onCreate()..." , 0).show();
		}*/
	    //Toast.makeText(mContext, "onCreate()..." , 0).show();
	    citySize = db.getCities();
	    if(citySize == 0){
	    	sendMsg(TOADDLOCATIONACTIVITY);
	    	return;
	    }
        
		requestQueue = Volley.newRequestQueue(this);
		dialog = new ProgressDialog(mContext);  
		
		popup_menu = (LinearLayout) findViewById(R.id.popup_menu_layout);
		menu = (ImageView) findViewById(R.id.menu);
		moreTv = (TextView) findViewById(R.id.moreTv);
		rootContainer = (LinearLayout) findViewById(R.id.root_container);
		moreTv.setOnClickListener(this);
		findView(popup_menu);
		setListener();		
		
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mVideoView = (VideoView) findViewById(R.id.videoView);
		mImageView = (ImageView) findViewById(R.id.imageView);
				
		viewLists = new ArrayList<View>();
		views = new View[maxCitySize];
		ccs = new CurrentConditions[maxCitySize];
		foreDayLists = new ArrayList<Day>();
		
		mobileLink_days = new String[ForeSize];
		weekDays = new String[ForeSize];
		dayImages = new int[ForeSize];
		foreweathertxts = new String[ForeSize];
		highs = new int[ForeSize];		
		lows = new int[ForeSize];
		
		linearLayout_day = new LinearLayout[ForeSize];
		Tv_week = new TextView[ForeSize];
		Iv_day = new ImageView[ForeSize];
		marq_weathertxt = new MarqueeTextView[ForeSize];
		Tv_high = new TextView[ForeSize];Tv_low = new TextView[ForeSize];
		
		adapter = new MyPagerAdapter(viewLists);
	    		
		AddViews();
		/*int index = viewPager.getCurrentItem();
		if("unknown".equals(mCurrentKey)){
			index = citySize - 1;
		}else {
			for(int i = 0; i < citySize; i++){
				if(mCurrentKey.equals(ccs[i].getKey())){
					index = i;
					break;
				}
			}
		}
		viewPager.setCurrentItem(index);		
	    initBackgroundIcon( Boolean.parseBoolean(ccs[index].getDayTime()) ,new Integer(ccs[index].getIcon()));*/
		
	    viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int arg0) {
				//Toast.makeText(MainActivity.this, "onPageSelected and arg0 is :" + arg0, 0).show();
				
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//Toast.makeText(MainActivity.this, "onPageScrolled and arg0 is :" + arg0 + ",and arg2 is :" +arg2, 0).show();
				//int index = viewPager.getCurrentItem();
				Log.i(TAG, "arg0 is " + arg0);
				if(db.getCities() != 0){
					currentCity_editor.putString("currentkey", ccs[arg0].getKey());
					currentCity_editor.commit();
					mobileLink = ccs[arg0].getMobileLink();
					for(int i = 0; i < ForeSize; i++){
						mobileLink_days[i] = (db.query_forecasts(ccs[arg0].getKey(), String.valueOf(i + 2))).getMobileLink();
					}
					initBackgroundIcon( Boolean.parseBoolean(ccs[arg0].getDayTime()) ,new Integer(ccs[arg0].getIcon()));			    
					//AddProgressIcon();
					for(int i = 0; i < rootContainer.getChildCount(); i++){
						((ImageView)rootContainer.getChildAt(i)).setImageResource(non_current_progress_icon);
					}
					((ImageView)rootContainer.getChildAt(arg0)).setImageResource(current_progress_icon);	
					sendCurrentCityChangedBroadcast();					
				}
			}
			
			public void onPageScrollStateChanged(int arg0) {
				//Toast.makeText(MainActivity.this, "onPageScrollStateChanged and arg0 is :" + arg0, 0).show();	
			}
		});
	    
	    AddProgressIcon();
	    sendCurrentCityChangedBroadcast();
	}
	
	public void sendCurrentCityChangedBroadcast(){
		Intent i = new Intent();
	    i.setAction(CURRENT_CITY_CHANGED_BROADCAST);
	    sendBroadcast(i);
	}
	
	public void doGetForecastsThread(){
		mCurrentKey = currentCity_preference.getString("currentkey", "unknown");
		Thread thread_forecasts = new Thread(){

			@Override
			public void run() {
				Log.i(TAG, "start to getForecast...");
				sendForecastDailyWeatherFindRequest(mCurrentKey, null, null, "zh-cn");									
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
	    feedbackDayLists = new ArrayList<Day>();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(forecastUrl, null,
				new Response.Listener<JSONObject>() {

					public void onResponse(JSONObject response) {
						Log.i(TAG,"get5DayForecastData is:" + response);
						feedbackDayLists = responseUtil.getForecastFromJSON(MainActivity.this, response);	
                    	for(int i = 0; i < ForeSize; i++){
                    		(feedbackDayLists.get(i)).setKey(locationKey);
            				(feedbackDayLists.get(i)).setDayNum(String.valueOf(i + 2));
            			}
                    	Log.i(TAG,"feedbackDayLists is:" + feedbackDayLists);
                    	//sendMsg(UNPOPUP_PROGRESS_DIALOG);
						if(null == feedbackDayLists || feedbackDayLists.size() == 0){
							sendMsg(UNPOPUP_PROGRESS_DIALOG);
							sendMsg(UPDATE_CURRENTCONDITIONS_FAIL);	
						}else {		
							Log.i(TAG,"refresh success!");
							sendMsg(UNPOPUP_PROGRESS_DIALOG);		
							sendMsg(UPDATEDB);							
							sendMsg(UPDATE_DATA);
							sendMsg(UPDATE_CURRENTCONDITIONS_SUCCESS);
						}						
					}
				}, 
				new Response.ErrorListener() {

					public void onErrorResponse(VolleyError error) {
						//sendMsg(UNPOPUP_PROGRESS_DIALOG);
						sendMsg(UPDATE_CURRENTCONDITIONS_FAIL);						
					}
				});
		jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);	
    }
	
	public void sendUnitChangedBroadcast(){
		Intent i = new Intent();
	    i.setAction(UNIT_CHANGED_BROADCAST);
	    sendBroadcast(i);
	}
	
	public void AddProgressIcon(){
		ImageView [] imgs = new ImageView[maxCitySize];
		ImageView imageView;
		int imgId;
		android.view.ViewGroup.LayoutParams params;
		if(rootContainer.getChildCount() != 0) {
			rootContainer.removeAllViews();
		}
		for(int i = 0; i < db.getCities(); i++){
			imageView = new ImageView(mContext);
			params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			imageView.setLayoutParams(params);
			imageView.setPadding(3, 0, 3, 0);
			if(i == viewPager.getCurrentItem()){
				imgId = current_progress_icon;
			}else {
				imgId = non_current_progress_icon;
			}
			imageView.setImageResource(imgId);
			rootContainer.addView(imageView);
		}
		
	}
	
	public void AddViews(){
		citySize = db.getCities();
		LayoutInflater inflater = getLayoutInflater();
		if(!viewLists.isEmpty()){
			viewLists.clear();
		}
		ArrayList<String> savedKeyLists = db.getSavedKeys();		
		for(int i = 0; i < citySize; i++){	
			foreDayLists.clear();
	        views[i] = inflater.inflate(R.layout.forecast, null); 
	    	
			ccs[i] = getCurrentConditionsFromCursor(db.queryByLine(i));
	        //ccs[i] = db.query(savedKeyLists.get(i));
			for(int n = 0; n < ForeSize ; n++){
				foreDayLists.add(db.query_forecasts(ccs[i].getKey(), String.valueOf(n + 2)));				
			}
			
			findView(views[i]);
			
			getSettings();
			getDataFromCC(ccs[i]);
			getDataFromForeCast(foreDayLists);
			
			initDataAndUnits(ccs[i]);
			initDataAndUnitsFromForeCast(foreDayLists);
			setTexts();
			//initBackgroundIcon(iconNum);	
			viewLists.add(views[i]);
	    }
		adapter.notifyDataSetChanged();
		adapter = new MyPagerAdapter(viewLists);
		viewPager.setAdapter(adapter);
				
		mCurrentKey = currentCity_preference.getString("currentkey", "unknown");
		int index = viewPager.getCurrentItem();
		if("unknown".equals(mCurrentKey)){
			index = citySize - 1;
		}else {
			for(int i = 0; i < citySize; i++){
				if(mCurrentKey.equals(ccs[i].getKey())){
					index = i;
					break;
				}
			}
		}
		viewPager.setCurrentItem(index);		
	    initBackgroundIcon( Boolean.parseBoolean(ccs[index].getDayTime()) ,new Integer(ccs[index].getIcon()));
	    mobileLink = ccs[index].getMobileLink();
	    for(int i = 0; i < ForeSize; i++){
			mobileLink_days[i] = (db.query_forecasts(ccs[index].getKey(), String.valueOf(i + 2))).getMobileLink();
		}
	}
	
	public void AddViews2(){
		citySize = db.getCities();
		ArrayList<Day> mDayLists = null;
		int index = viewPager.getCurrentItem();
		for(int i = 0; i < citySize; i++){		    	
			//cc = getCurrentConditionsFromCursor(db.queryByLine(i));
			findView(viewLists.get(i));
			
			getSettings();
			if(null != ccs[i]){
				getDataFromCC(ccs[i]);
			}
			mDayLists = new ArrayList<Day>();
			for(int j = 0 ; j < ForeSize; j++){
				mDayLists.add(db.query_forecasts(ccs[i].getKey(), String.valueOf(j + 2)));
			}
			getDataFromForeCast(mDayLists);
			initDataAndUnits(ccs[i]);
			initDataAndUnitsFromForeCast(mDayLists);
			setTexts();
			//initBackgroundIcon(iconNum);	
			//viewLists.add(views[i]);
	    }
		adapter.notifyDataSetChanged();
		adapter = new MyPagerAdapter(viewLists);
		viewPager.setAdapter(adapter);	
		viewPager.setCurrentItem(index);
		sendUnitChangedBroadcast();
	}
		
	public void findView(View v){
		if(v == popup_menu){
			//popup_menu = (LinearLayout) v.findViewById(R.id.popup_menu_layout);
			menuAddLocation = (TextView) popup_menu.findViewById(R.id.menu_add_location);
			menuDeleteLocation = (TextView) popup_menu.findViewById(R.id.menu_delete_location);
			menuRefresh = (TextView) popup_menu.findViewById(R.id.menu_refresh);
			menuTempUnit = (TextView) popup_menu.findViewById(R.id.menu_temp_unit);
			menuDistUnit = (TextView) popup_menu.findViewById(R.id.menu_dist_unit);
		}else{
			dateTv = (TextView) v.findViewById(R.id.date_Tv);
			dayTv = (TextView) v.findViewById(R.id.day_Tv);
			timeTv = (TextView) v.findViewById(R.id.time_Tv);
			temperatureLabel = (TextView) v.findViewById(R.id.temperatureLabel);
			weathertextLabel = (TextView) v.findViewById(R.id.weathertextLabel);
			cityLabel = (MarqueeTextView) v.findViewById(R.id.cityLabel);
			
			realfeelTv = (TextView) v.findViewById(R.id.realfeel_data);
			humidityTv = (TextView) v.findViewById(R.id.humidity_data);
			windspeedTv = (TextView) v.findViewById(R.id.windspeed_data);
			visibilityTv = (TextView) v.findViewById(R.id.visibility_data);
			
			realfeelUnitTv = (TextView) v.findViewById(R.id.realfeel_unit);
			windspeedUnitTv = (TextView) v.findViewById(R.id.windspeed_unit);
			visibilityUnitTv = (TextView) v.findViewById(R.id.visibility_unit);	
			
			linearLayout_day[0] = (LinearLayout) v.findViewById(R.id.day1_linear);
			linearLayout_day[1] = (LinearLayout) v.findViewById(R.id.day2_linear);
			linearLayout_day[2] = (LinearLayout) v.findViewById(R.id.day3_linear);
			linearLayout_day[3] = (LinearLayout) v.findViewById(R.id.day4_linear);
			
			Tv_week[0] = (TextView) v.findViewById(R.id.Tv_week1_forecast);
			Tv_week[1] = (TextView) v.findViewById(R.id.Tv_week2_forecast);
			Tv_week[2] = (TextView) v.findViewById(R.id.Tv_week3_forecast);
			Tv_week[3] = (TextView) v.findViewById(R.id.Tv_week4_forecast);
			
			Iv_day[0] = (ImageView) v.findViewById(R.id.Iv_day1_forecast);
			Iv_day[1] = (ImageView) v.findViewById(R.id.Iv_day2_forecast);
			Iv_day[2] = (ImageView) v.findViewById(R.id.Iv_day3_forecast);
			Iv_day[3] = (ImageView) v.findViewById(R.id.Iv_day4_forecast);
			
			marq_weathertxt[0] = (MarqueeTextView) v.findViewById(R.id.marqweathertxt1);
			marq_weathertxt[1] = (MarqueeTextView) v.findViewById(R.id.marqweathertxt2);
			marq_weathertxt[2] = (MarqueeTextView) v.findViewById(R.id.marqweathertxt3);
			marq_weathertxt[3] = (MarqueeTextView) v.findViewById(R.id.marqweathertxt4);
			
			Tv_high[0] = (TextView) v.findViewById(R.id.Tv_high1_forecast); Tv_low[0] = (TextView) v.findViewById(R.id.Tv_low1_forecast);
			Tv_high[1] = (TextView) v.findViewById(R.id.Tv_high2_forecast); Tv_low[1] = (TextView) v.findViewById(R.id.Tv_low2_forecast);
			Tv_high[2] = (TextView) v.findViewById(R.id.Tv_high3_forecast); Tv_low[2] = (TextView) v.findViewById(R.id.Tv_low3_forecast);
			Tv_high[3] = (TextView) v.findViewById(R.id.Tv_high4_forecast); Tv_low[3] = (TextView) v.findViewById(R.id.Tv_low4_forecast);
				
			//menu = (ImageView) v.findViewById(R.id.menu);		
			//mVideoView = (VideoView) v.findViewById(R.id.videoView);
			//mImageView = (ImageView) v.findViewById(R.id.imageView);
		}
	}
	
	public void setListener(){
		menu.setOnClickListener(this);
		menuAddLocation.setOnClickListener(this);
		menuDeleteLocation.setOnClickListener(this);
		menuRefresh.setOnClickListener(this);
		menuTempUnit.setOnClickListener(this);
		menuDistUnit.setOnClickListener(this);
	}
	
	public CurrentConditions getCurrentConditionsFromCursor(Cursor cursor){
		CurrentConditions current;
		if(cursor.moveToFirst()){
			String key = cursor.getString(cursor.getColumnIndex(WeatherDB.CITY_KEY));
			String name = cursor.getString(cursor.getColumnIndex(WeatherDB.CITY_NAME));
			String icon = cursor.getString(cursor.getColumnIndex(WeatherDB.ICON));
			String dayTime = cursor.getString(cursor.getColumnIndex(WeatherDB.DAYTIME));
			String tempC = cursor.getString(cursor.getColumnIndex(WeatherDB.TEMPC));
			String tempF = cursor.getString(cursor.getColumnIndex(WeatherDB.TEMPF));
			String weathertext = cursor.getString(cursor.getColumnIndex(WeatherDB.WEATHERTEXT));
			String realC = cursor.getString(cursor.getColumnIndex(WeatherDB.REALFEELC));
			String realF = cursor.getString(cursor.getColumnIndex(WeatherDB.REALFEELF));
			String humidity = cursor.getString(cursor.getColumnIndex(WeatherDB.HUMIDITY));
			String windspeedKM = cursor.getString(cursor.getColumnIndex(WeatherDB.WINDSPEEDKM));
			String windspeedMI = cursor.getString(cursor.getColumnIndex(WeatherDB.WINDSPEEDMI));
			String visibleKM = cursor.getString(cursor.getColumnIndex(WeatherDB.VISIBILITYKM));
			String visibleMI = cursor.getString(cursor.getColumnIndex(WeatherDB.VISIBILITYMI));
			String mobileLink = cursor.getString(cursor.getColumnIndex(WeatherDB.MOBLIELINK));
			String date = cursor.getString(cursor.getColumnIndex(WeatherDB.DATE));
			String day = cursor.getString(cursor.getColumnIndex(WeatherDB.DAY));
			String time = cursor.getString(cursor.getColumnIndex(WeatherDB.TIME));
			
			editor.putString("visiblekm", visibleKM);
			editor.commit();
			current = new CurrentConditions();
			current.setKey(key);
			current.setName(name);
			current.setIcon(icon);
			current.setDayTime(dayTime);
			current.setTemperatureC(tempC);
			current.setTemperatureF(tempF);
			current.setWeathertext(weathertext);
			current.setRealfeelC(realC);
			current.setRealfeelF(realF);
			current.setHumidity(humidity);
			current.setWindspeedKM(windspeedKM);
			current.setWindspeedMI(windspeedMI);
			current.setVisibilityKM(visibleKM);
			current.setVisibilityMI(visibleMI);
			current.setMobileLink(mobileLink);
			current.setDate(date);
			current.setDay(day);
			current.setTime(time);
			
			return current;
		}
		return null;
    }
	
	public void getDataFromCC(CurrentConditions cc){
		if(null != cc){
			iconNum = new Integer(Integer.parseInt(cc.getIcon()));
			isDayTime = Boolean.parseBoolean(cc.getDayTime());
			
			editor.putString("num", "" + iconNum.intValue());
			editor.putString("isDayTime", "" + isDayTime);
			editor.commit();
			
			cityName = cc.getName();
			cityKey = cc.getKey();
			weathertext = cc.getWeathertext();
			humidity = Math.round(Float.parseFloat(cc.getHumidity()));
			mobileLink = cc.getMobileLink();
			date = cc.getDate();
			weekDay = cc.getDay();
			time = cc.getTime();
		}
	}
	
	public void getDataFromForeCast(ArrayList<Day> mDayLists){
		if(null != mDayLists && mDayLists.size() != 0){
			for (int i = 0; i < ForeSize; i++) {
				weekDays[i] = (mDayLists.get(i)).getWeek();
				dayImages[i] = Integer.parseInt((mDayLists.get(i)).getIconNumber());
				foreweathertxts[i] = (mDayLists.get(i)).getIconPhrase();
			}
		}
	}
	
	public void getSettings(){		
		CF = settings_preferences.getString("cf", "c");
		KMMI = settings_preferences.getString("kmmi", "km");
	}
	
	public void initDataAndUnits(CurrentConditions cc){
		if("c".equals(CF)){
			temperature = Math.round(Float.parseFloat(cc.getTemperatureC()));				
			realfeel = Math.round(Float.parseFloat(cc.getRealfeelC()));				
			realfeelUnitLabel = getResources().getString(R.string.c);
		}else{
			temperature = Math.round(Float.parseFloat(cc.getTemperatureF()));				
			realfeel = Math.round(Float.parseFloat(cc.getRealfeelF()));	
			realfeelUnitLabel = getResources().getString(R.string.f);
		}
		
		if("km".equals(KMMI)){
			windspeed = Float.parseFloat(cc.getWindspeedKM());
			visibility = Float.parseFloat(cc.getVisibilityKM());
			windspeedUnitLabel = getResources().getString(R.string.kmspeed);
			visibleUnitLabel = getResources().getString(R.string.kmvisible);
			
		}else {
			windspeed = Float.parseFloat(cc.getWindspeedMI());
			visibility = Float.parseFloat(cc.getVisibilityMI());
			windspeedUnitLabel = getResources().getString(R.string.mispeed);
			visibleUnitLabel = getResources().getString(R.string.mivisible);
		}
	}
	
	public void initDataAndUnitsFromForeCast(ArrayList<Day> mList){
		for(int i = 0; i < ForeSize ; i++){
			if("c".equals(CF)){
				highs[i] = Math.round(Float.parseFloat(mList.get(i).getHighTempC()));
				lows[i] = Math.round(Float.parseFloat(mList.get(i).getLowTempC()));
			}else{
				highs[i] = Math.round(Float.parseFloat(mList.get(i).getHighTempF()));
				lows[i] = Math.round(Float.parseFloat(mList.get(i).getLowTempF()));
			}			
		}
	}
	
	@SuppressLint("UseValueOf") public void setTexts(){
		dateTv.setText(date);
		dayTv.setText(weekDay);
		timeTv.setText(time);
		temperatureLabel.setText("" + temperature);
		weathertextLabel.setText("" +weathertext);
		Log.i(TAG,"cityname is:" + cityName);
		cityLabel.setText("" + cityName);
		cityLabel.setTextSize(new Float(22.0));
		realfeelTv.setText("" + realfeel);
		realfeelUnitTv.setText("" + realfeelUnitLabel);
		humidityTv.setText("" + humidity);
		windspeedTv.setText("" + windspeed);
		windspeedUnitTv.setText("" + windspeedUnitLabel);
		visibilityTv.setText("" + visibility);
		visibilityUnitTv.setText("" + visibleUnitLabel);
		
		for(int i = 0; i < ForeSize; i++){
			Tv_week[i].setText(weekDays[i]);
			Iv_day[i].setImageResource(IconBackgroundUtil.getForecastImg(new Integer(dayImages[i])));
			marq_weathertxt[i].setText(foreweathertxts[i]);
			Tv_high[i].setText(String.valueOf(highs[i]));Tv_low[i].setText(String.valueOf(lows[0]));			
		}
		
		/*Tv_week2.setText(weekDays[1]);Iv_day2.setImageResource(IconBackgroundUtil.getForecastImg(new Integer(dayImages[1])));
		Tv_high2.setText(String.valueOf(highs[1]));Tv_low2.setText(String.valueOf(lows[1]));
		
		Tv_week3.setText(weekDays[2]);Iv_day3.setImageResource(IconBackgroundUtil.getForecastImg(new Integer(dayImages[2])));
		Tv_high3.setText(String.valueOf(highs[2]));Tv_low3.setText(String.valueOf(lows[2]));
		
		Tv_week4.setText(weekDays[3]);Iv_day4.setImageResource(IconBackgroundUtil.getForecastImg(new Integer(dayImages[3])));
		Tv_high4.setText(String.valueOf(highs[3]));Tv_low4.setText(String.valueOf(lows[3]));*/
	}
	
	public void initBackgroundIcon(Boolean isItDayTime, Integer mInteger){
		if(!isUsePic){
			mVideoView.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.INVISIBLE);
			backgroundIconUri = IconBackgroundUtil.getBackgroundUri(mInteger);
			displayBackgroundIcon();
		}else {
			if(null != mVideoView && mVideoView.VISIBLE == View.VISIBLE && mVideoView.isPlaying()){
				mVideoView.stopPlayback();
			}
			mImageView.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.INVISIBLE);
			imgNum = IconBackgroundUtil.getBackgroundImg(mInteger);
			displayBackgroundImg();					
		}
		
	}
	
	public void displayBackgroundImg(){
		mImageView.setBackgroundResource(imgNum.intValue());
	}
	
	public void displayBackgroundIcon(){
		new Thread(){

			@Override
			public void run() {
				try {
					play();	
				} catch (Exception e) {
					editor.putString("exception", "" + e);
					editor.commit();
				}
				super.run();
			}
			
		}.start();			
	}
	
	private void play() throws IOException{	
		if(null != mVideoView && mVideoView.VISIBLE == View.VISIBLE && mVideoView.isPlaying()){
			mVideoView.stopPlayback();			
		}
		mVideoView.setVideoURI(backgroundIconUri);		
		mVideoView.start();
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				mp.setLooping(true);
			}
		});
		
		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			public void onCompletion(MediaPlayer mp) {
				mVideoView.setVideoURI(backgroundIconUri);		
				mVideoView.start();				
			}
		});
	}
		
	@Override
	protected void onDestroy() {
		if(null != mVideoView && mVideoView.VISIBLE == View.VISIBLE && mVideoView.isPlaying()){
			mVideoView.stopPlayback();			
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {		
		super.onPause();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu:		
			//showPopupWindow();
			showPopupMenu();
			break;
		case R.id.menu_add_location:
			hidePopupWindow();
			add();
			break;
		case R.id.menu_delete_location:
			hidePopupWindow();
			remove();
			break;
		case R.id.menu_refresh:
			hidePopupWindow();
			refresh();
			break;
		case R.id.menu_temp_unit:
			if("c".equals(CF)){
				editor.putString("cf", "f");
			}else {
				editor.putString("cf", "c");
			}
			editor.commit();
			/*getSettings();
			initDataAndUnits();
			setTexts();	*/
			
			hidePopupWindow();
			AddViews2();	
			break;
		case R.id.menu_dist_unit:
			if("km".equals(KMMI)){
				editor.putString("kmmi", "mi");
			}else {
				editor.putString("kmmi", "km");
			}
		    editor.commit();
			/*getSettings();
			initDataAndUnits();
			setTexts();*/
			
			hidePopupWindow();
			AddViews2();
			break;
		case R.id.moreTv:
			openLink(mobileLink);
			break;
		case R.id.day1_linear:
			openLink(mobileLink_days[0]);
			break;
		case R.id.day2_linear:
			openLink(mobileLink_days[1]);
			break;
		case R.id.day3_linear:
			openLink(mobileLink_days[2]);
			break;
		case R.id.day4_linear:
			openLink(mobileLink_days[3]);
			break;
		default:
			break;
		}
		
	}
	
	public void openLink(String url){
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	public void popupProgressDialogOrNot(int flag){
    	if(flag == UNPOPUP_PROGRESS_DIALOG){    		
    		dialog.dismiss();
    	}else if(flag == POPUP_PROGRESS_DIALOG){    		
    		dialog.setMessage(getResources().getString(R.string.update));
    		dialog.setProgressStyle(dialog.STYLE_SPINNER);
    		dialog.setIndeterminate(false);
    		dialog.setCancelable(false);
    		dialog.show();
    	}
    }
	
	public void showToast(int flag){
    	String info = "";
    	switch (flag) {
	    	case NETWORK_ERROR:
	    		info = getResources().getString(R.string.network_error);
	    		break;
	    	case UPDATE_CURRENTCONDITIONS_FAIL:
	    		info = getResources().getString(R.string.refresh_fail);
	    		break;
	    	case UPDATE_CURRENTCONDITIONS_SUCCESS:
	    		info = getResources().getString(R.string.refresh_success);
	    		break;
	    	case OVERMAXCITYWARNING:
	    		info = getResources().getString(R.string.over_max_city_warning);
	    		break;
	    	default:
	    		break;
    	}
    	Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
    }
	
	public void initForecastWeather(){
		//weeks = new TextView[]{Tv_week1, Tv_week2, Tv_week3, Tv_week4};
		//temps = new TextView[][]{{Tv_high1,Tv_low1},{Tv_high2,Tv_low2},{Tv_high3,Tv_low3},{Tv_high4,Tv_low4}};
		
		/*if(null != dayList){
			for(int i = 1; i< dayList.size(); i++){
				day = dayList.get(i);
				weeks[i-1].setText(day.getWeek());				
				temps[i-1][0].setText(day.getHighTemp());
				temps[i-1][1].setText(day.getLowTemp());
			}
		}*/
	}

	public void setMenuUnitString(){
		getSettings();
		if("c".equals(CF)){
			menuTempUnit.setText(getResources().getString(R.string.change_to_f));
		}else {
			menuTempUnit.setText(getResources().getString(R.string.change_to_c));
		}
		
		if("km".equals(KMMI)){
			menuDistUnit.setText(getResources().getString(R.string.change_to_mi));
		}else {
			menuDistUnit.setText(getResources().getString(R.string.change_to_km));
		}
	}
	
	private void sendMsg(int flag){
    	Message msg = new Message();
    	msg.what = flag;
    	mHandler.sendMessage(msg);
    }
	
	public void add(){
		citySize = db.getCities();
		if(citySize >= maxCitySize){
			sendMsg(OVERMAXCITYWARNING);
		}else {
			sendMsg(TOADDLOCATIONACTIVITY);			
		}
	}
	
	public void remove(){
		int currentIndex = viewPager.getCurrentItem();
		int citySize = db.getCities();
		//ArrayList<String> savedKeyLists = db.getSavedKeys();	
		ccs = new CurrentConditions[maxCitySize];
		for(int i = 0; i < citySize; i++){	    	
			ccs[i] = getCurrentConditionsFromCursor(db.queryByLine(i));
			//ccs[i] = db.query(savedKeyLists.get(i));
	    }
		db.delete(ccs[currentIndex]);
		//viewLists.remove(views[currentIndex]);
		db.delete_forecasts(ccs[currentIndex].getKey());
		viewLists.remove(currentIndex);
		rootContainer.removeViewAt(currentIndex);
		adapter = new MyPagerAdapter(viewLists);
		viewPager.setAdapter(adapter);
		
		if(citySize == 1){
			currentCity_editor.putString("currentkey", "unknown");
			currentCity_editor.commit();			
			sendMsg(TOADDLOCATIONACTIVITY);
			sendCurrentCityChangedBroadcast();
			finish();
			return;
		}
		int newIndex;
		if(currentIndex >= citySize - 1){
			currentIndex = 0;			
			newIndex = 0;
			//initBackgroundIcon( Boolean.parseBoolean(ccs[0].getDayTime()) ,
			//		new Integer(ccs[0].getIcon()));
		}else {
			currentIndex = currentIndex;
			newIndex = currentIndex + 1;
			//initBackgroundIcon( Boolean.parseBoolean(ccs[currentIndex + 1].getDayTime()) ,
			//		new Integer(ccs[currentIndex + 1].getIcon()));			
		}
		viewPager.setCurrentItem(currentIndex);
		initBackgroundIcon( Boolean.parseBoolean(ccs[newIndex].getDayTime()) ,
					new Integer(ccs[newIndex].getIcon()));
		currentCity_editor.putString("currentkey", ccs[newIndex].getKey());
		currentCity_editor.commit();
		sendCurrentCityChangedBroadcast();
				
		ImageView iv = (ImageView)rootContainer.getChildAt(currentIndex);
		iv.setImageResource(current_progress_icon);
		
		ccs = new CurrentConditions[maxCitySize];
		//savedKeyLists = db.getSavedKeys();
		citySize = db.getCities();
		for(int i = 0; i < citySize; i++){	    	
			ccs[i] = getCurrentConditionsFromCursor(db.queryByLine(i));
			//ccs[i] = db.query(savedKeyLists.get(i));
	    }
	}
	
	public void refresh(){
		checkNetwork = new CheckNetwork(mContext);
		if(!checkNetwork.isNetworkAvailable()){
			sendMsg(UNPOPUP_PROGRESS_DIALOG);
			sendMsg(NETWORK_ERROR);
		}else {							
			Thread thread = new Thread(){
				
				@Override
				public void run() {					
					mCurrentKey = currentCity_preference.getString("currentkey", "unknown");
					if(!mCurrentKey.equals("unknown")){
						CurrentConditions conditions = db.query(mCurrentKey);
						sendCurrentConditionsFindRequest(conditions.getName(), conditions.getKey(), null, null, "zh-cn");			
					}
				}
				
			};
			thread.start();				
		}
	}
	
	public void sendCurrentConditionsFindRequest(final String selectedCityName, final String selectedCityKey,
			String lantitude, String longitude, String language){
		urlRequest = new UrlRequest();
		String cityKeyUrl = urlRequest.currentWeatherUrl(selectedCityKey, lantitude, longitude, language);
		responseUtil = new ResponseUtil();
		sendMsg(POPUP_PROGRESS_DIALOG);	
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(cityKeyUrl, 
				new Response.Listener<JSONArray>() {

					public void onResponse(JSONArray response) {  
						updateConditions = responseUtil.getCurrentConditionsFromJSON(mContext, response);
						if(null == updateConditions){
							sendMsg(UPDATE_CURRENTCONDITIONS_FAIL);		
							editor.putString("info", "currentIsNull");
							editor.commit();
							Log.i(TAG,"current is null");
						}else {
							updateConditions.setKey(selectedCityKey);
							updateConditions.setName(selectedCityName);
							Log.i(TAG,"current is :" + updateConditions);	
							//sendMsg(UPDATEDB);							
							//sendMsg(UPDATE_DATA);
							//sendMsg(UPDATE_CURRENTCONDITIONS_SUCCESS);	
							sendMsg(DOGETFORECASTSTHREAD);
						}
						sendMsg(UNPOPUP_PROGRESS_DIALOG);
					}
				}, 
				new Response.ErrorListener() {

					public void onErrorResponse(VolleyError error) {
						sendMsg(UNPOPUP_PROGRESS_DIALOG);
						sendMsg(UPDATE_CURRENTCONDITIONS_FAIL);		
						editor.putString("info", "onErrorResponse" + error);
						editor.commit();
					}
				});
		jsonArrayRequest.setShouldCache(false);
        requestQueue.add(jsonArrayRequest);	      
    }
	
	public void showPopupMenu(){
		popup_menu.setVisibility(View.VISIBLE);
		setMenuUnitString();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();

            if (null != popup_menu && popup_menu.getVisibility() == View.VISIBLE) {
                Rect hitRect = new Rect();
                popup_menu.getGlobalVisibleRect(hitRect);
                if (!hitRect.contains(x, y)) {
                    popup_menu.setVisibility(View.GONE);
                    return true;
                }
            }
        }
		return super.dispatchTouchEvent(ev);
        
	}

	public void showPopupWindow(){
		LayoutInflater inflater = getLayoutInflater();
		View popupLayout = inflater.inflate(R.layout.popup_menu, null);
		popupWindow = new PopupWindow();
		popupWindow.setContentView(popupLayout);
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true); 
		
		int paddingTop = (int) getResources().getDimension(R.dimen.rootmargintop);
		int padding = (int) getResources().getDimension(R.dimen.rootmargin);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int screenSize = (int)(wm.getDefaultDisplay().getWidth() / (getResources().getDisplayMetrics().density) + 0.5f);
		
		popupWindow.showAtLocation(findViewById(R.id.menu), 
				Gravity.NO_GRAVITY, (int)menu.getX(), (int)menu.getY() + paddingTop);	
		editor.putInt("x", screenSize);
		editor.putInt("y", (int)menu.getY());
		editor.commit();
		
		menuAddLocation = (TextView) popupLayout.findViewById(R.id.menu_add_location);
		menuDeleteLocation = (TextView) popupLayout.findViewById(R.id.menu_delete_location);
		menuRefresh = (TextView) popupLayout.findViewById(R.id.menu_refresh);
		menuTempUnit = (TextView) popupLayout.findViewById(R.id.menu_temp_unit);
		menuDistUnit = (TextView) popupLayout.findViewById(R.id.menu_dist_unit);
		
		setMenuUnitString();
		
		menuAddLocation.setOnClickListener(this);
		menuDeleteLocation.setOnClickListener(this);
		menuRefresh.setOnClickListener(this);
		menuTempUnit.setOnClickListener(this);
		menuDistUnit.setOnClickListener(this);		
		
	}
	
	public void hidePopupWindow(){		
		/*if(popupWindow.isShowing()){
			popupWindow.dismiss();
		}*/
		if(popup_menu.getVisibility() == View.VISIBLE){
			popup_menu.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onRestart() {
		//Toast.makeText(mContext, "onRestart()..." , 0).show();
		//String widgetLocationKey =  getIntent().getStringExtra("widgetLocationKey");
		/*if(!"".equals(widgetLocationKey) && null != widgetLocationKey){
			mCurrentKey = widgetLocationKey;
			currentCity_editor.putString("currentkey", mCurrentKey);
			currentCity_editor.commit();
			//Toast.makeText(mContext, "onCreate()..." , 0).show();
			
		}*/
		mCurrentKey = currentCity_preference.getString("currentkey", "unknown");
		int index = 0;
		int size = db.getCities();
		if("unknown".equals(mCurrentKey)){
			index = size - 1;
		}else {
			for(int i = 0; i < size; i++){
				if(mCurrentKey.equals(ccs[i].getKey())){
					index = i;
					break;
				}
			}
		}
		initBackgroundIcon( Boolean.parseBoolean(ccs[index].getDayTime()) ,new Integer(ccs[index].getIcon()));
	    mobileLink = ccs[index].getMobileLink();
	    for(int i = 0; i < ForeSize; i++){
			mobileLink_days[i] = (db.query_forecasts(ccs[index].getKey(), String.valueOf(i + 2))).getMobileLink();
		}
	    ArrayList<Day> mDayLists = null;
	    for(int i = 0; i < size; i++){		    	
			//ccs[i] = getCurrentConditionsFromCursor(db.queryByLine(i));
	        //ccs[i] = db.query(savedKeyLists.get(i));
			findView(views[i]);
			
			getSettings();
			if(null != ccs[i]){
				getDataFromCC(ccs[i]);
			}
			initDataAndUnits(ccs[i]);
			mDayLists = new ArrayList<Day>();
			for(int j = 0 ; j < ForeSize; j++){
				mDayLists.add(db.query_forecasts(ccs[i].getKey(), String.valueOf(j + 2)));
			}
			getDataFromForeCast(mDayLists);
			initDataAndUnitsFromForeCast(mDayLists);
			setTexts();
	    }
		adapter.notifyDataSetChanged();
		adapter = new MyPagerAdapter(viewLists);
		viewPager.setAdapter(adapter);
		
		viewPager.setCurrentItem(index);	
			    		
		super.onRestart();
	}

}
