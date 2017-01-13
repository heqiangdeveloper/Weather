package com.heqiang.weather;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response.Listener;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WeatherWidgetProvider extends AppWidgetProvider {
	private WeatherDB db;
	private int temperature;
	private String unit, weatherText, location;
	private String currentKey, widgetLocationKey;
	private SharedPreferences currentCity_preferences, settings_preferences;
	private Editor currentCity_editor,settings_editor;
	private String TAG = "widgetlog";

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {		
		AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class));
        
        String action = intent.getAction();
        db = new WeatherDB(context);
        currentCity_preferences = context.getSharedPreferences("currentcity", context.MODE_PRIVATE);
        settings_preferences = context.getSharedPreferences("settings", context.MODE_PRIVATE);
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather_small_layout);	
        //for (int appWidgetId : appWidgetIds) {       	
    	if("android.appwidget.action.APPWIDGET_UPDATE".equals(action) ||
    			"com.heqiang.weather.MainActivity.CURRENTCITYCHANGEDBROADCAST".equals(action) ||
    			"com.heqiang.weather.MainActivity.UNITCHANGEDBROADCAST".equals(action)){  
    		//Toast.makeText(context, "mCurrentKey:" + widgetLocationKey, 0).show();
    		Log.i(TAG, "first create widget...");
    		currentKey = currentCity_preferences.getString("currentkey", "unknown");
    		remoteViewSettings(context,remoteViews,currentKey);  
    		for (int appWidgetId : appWidgetIds) {        			
    			mAppWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        	}
    	}
    	if("com.heqiang.weather.WeatherWidgetProvider.CHANGE_SMALL_WIDGET_TEMPERATURE".equals(action)){ 
    		currentKey = currentCity_preferences.getString("currentkey", "unknown");
			if(!"unknown".equals(currentKey)){
				CurrentConditions cc = db.query(currentKey);        				
				if(null != cc){    					
					String CF = settings_preferences.getString("cf", "c");      				
					String temperatureString = CF.equals("c")?cc.getTemperatureF():cc.getTemperatureC();
					temperature = Math.round(Float.parseFloat(temperatureString));
					unit = CF.equals("c")?context.getString(R.string.char_f):context.getString(R.string.char_c);
					remoteViews.setTextViewText(R.id.small_widget_temp_data, temperature + "");
					remoteViews.setTextViewText(R.id.small_widget_temp_unit, unit);
					settings_editor = settings_preferences.edit();
					settings_editor.putString("cf", CF.equals("c")?"f":"c");
					settings_editor.commit();    			
					remoteViewSettings(context,remoteViews,currentKey);
				}
				for (int appWidgetId : appWidgetIds) {
					mAppWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        		}
    		}
    	}
    	if("com.heqiang.weather.WeatherWidgetProvider.CHANGE_SMALL_WIDGET_LOCATION".equals(action)){
    		//widgetLocationKey = currentCity_preferences.getString("widgetLocationKey", "unknown");
    		//Toast.makeText(context, "widgetLocationKey:" + widgetLocationKey, 0).show();        		
			currentKey = currentCity_preferences.getString("currentkey", "unknown");
			ArrayList<String> savedKeys = db.getSavedKeys();
			int k = 0;
			a: for(int i = 0; i < savedKeys.size(); i++){
				if(currentKey.equals(savedKeys.get(i))){
					k = i;
					break a;
				}
			}
			if(k == savedKeys.size() - 1){
				k = 0;
			}else {
				k++;
			}
			currentKey = savedKeys.get(k);
			currentCity_editor = currentCity_preferences.edit();
			currentCity_editor.putString("currentkey", currentKey);
			currentCity_editor.commit();  					
			
			remoteViewSettings(context,remoteViews,currentKey);
			for (int appWidgetId : appWidgetIds) {
			   mAppWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    		}
		}
		super.onReceive(context, intent);
	}
	
	public void remoteViewSettings(Context context, RemoteViews remoteViews,String key){			
		int citySize = db.getCities();		
		if(citySize == 0){        			
			remoteViews.setViewVisibility(R.id.no_data_layout, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.has_data_layout, View.GONE);
			
			Intent selectPageIntent = new Intent(context, MainActivity.class);
			PendingIntent pSelectPageIntent = PendingIntent.getActivity(context, 0, selectPageIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.no_data_layout, pSelectPageIntent);
			//remoteViews.setOnClickPendingIntent(R.id.small_widget_icon, pSelectPageIntent);
		}else {
			remoteViews.setViewVisibility(R.id.no_data_layout, View.GONE);
			remoteViews.setViewVisibility(R.id.has_data_layout, View.VISIBLE);	
			
			CurrentConditions conditions = null;
			if(!"unknown".equals(key)){
				conditions = db.query(key);        				
				if(null != conditions){
					String CF = settings_preferences.getString("cf", "c");        				
					String temperatureString = CF.equals("c")?conditions.getTemperatureC():conditions.getTemperatureF();
					temperature = Math.round(Float.parseFloat(temperatureString));
					unit = CF.equals("c")?context.getString(R.string.char_c):context.getString(R.string.char_f);
					weatherText = conditions.getWeathertext();
					location = conditions.getName(); 
					remoteViews.setTextViewText(R.id.small_widget_temp_data, temperature + "");
					remoteViews.setTextViewText(R.id.small_widget_temp_unit, unit);
					remoteViews.setTextViewText(R.id.small_widget_details_weatherText, weatherText);
					remoteViews.setTextViewText(R.id.small_widget_details_cityName, location);        				
					remoteViews.setImageViewResource(R.id.small_widget_weather_icon, 
							IconBackgroundUtil.getSmallWidgetWhiteIcon(new Integer(conditions.getIcon())));					
				}
			}
			
			Intent changeTemperatureIntent = new Intent();
			changeTemperatureIntent.setAction("com.heqiang.weather.WeatherWidgetProvider.CHANGE_SMALL_WIDGET_TEMPERATURE");
			PendingIntent pChangeTemperatureIntent = PendingIntent.getBroadcast(context, 1, changeTemperatureIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.small_widget_temperature, pChangeTemperatureIntent);
			
			Intent selectPageIntent = new Intent(context, MainActivity.class);
			//selectPageIntent.putExtra("widgetLocationKey", key);
			PendingIntent pSelectPageIntent = PendingIntent.getActivity(context, 0, selectPageIntent, 0);
			//remoteViews.setOnClickPendingIntent(R.id.small_widget_temperature, pSelectPageIntent);			
			remoteViews.setOnClickPendingIntent(R.id.small_widget_icon, pSelectPageIntent);
			
			Intent changeLocationIntent = new Intent();
			changeLocationIntent.setAction("com.heqiang.weather.WeatherWidgetProvider.CHANGE_SMALL_WIDGET_LOCATION");
			PendingIntent pChangeLocationIntent = PendingIntent.getBroadcast(context, 2, changeLocationIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.small_widget_details, pChangeLocationIntent);	
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {	
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
