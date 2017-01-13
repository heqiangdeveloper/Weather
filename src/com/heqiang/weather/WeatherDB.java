package com.heqiang.weather;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDB extends SQLiteOpenHelper {
	
	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "weather.db";
	private static String TABLE_NAME_CURRENT = "current";
	public static final String ID = "_id";
	public static final String CITY_KEY = "key";
	public static final String CITY_NAME = "name";
	public static final String TEMPC = "tempc";
	public static final String TEMPF = "tempf";
	public static final String WEATHERTEXT = "text";
	public static final String REALFEELC = "realc";
	public static final String REALFEELF = "realf";
	public static final String HUMIDITY = "humidity";
	public static final String WINDSPEEDKM = "speedkm";
	public static final String WINDSPEEDMI = "speedmi";
	public static final String VISIBILITYKM = "visibkm";
	public static final String VISIBILITYMI = "visibmi";
	public static final String ICON = "icon";
	public static final String DAYTIME = "daytime";
	public static final String MOBLIELINK = "moblielink"; 
	public static final String DATE = "date"; 
	public static final String DAY = "day"; 
	public static final String TIME = "time"; 
	

	private static String TABLE_NAME_FORECASTS = "forecasts";
	public static final String WEEKNUM = "weeknum";
	public static final String ICONNUM= "iconnum";
	public static final String HIGHC = "highc";
	public static final String HIGHF = "highf";
	public static final String LOWC = "lowc";
	public static final String LOWF = "lowf";
	public static final String FOREMOBLIELINK = "foremoblielink";
	public static final String DAYNUM = "dayNum";
	public static final String ICONPHRASE = "iconphrase";
		
	private SQLiteDatabase db;
	private CurrentConditions current;

	public WeatherDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = this.getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		String sql_current = "CREATE TABLE " + TABLE_NAME_CURRENT + "(" +
	                       ID + " INTEGER PRIMARY KEY autoincrement," +
				           CITY_KEY + " text not null," + 
	                       CITY_NAME + " text not null," +
	                       WEATHERTEXT + " text not null," + 
	                       ICON + " text not null," + 
	                       DAYTIME + " text not null," +
	                       TEMPC + " text not null," + 
	                       TEMPF + " text not null," +
	                       REALFEELC + " text not null," +
	                       REALFEELF + " text not null," +
	                       HUMIDITY + " text not null," +
	                       WINDSPEEDKM + " text not null," +
	                       WINDSPEEDMI + " text not null," +
	                       VISIBILITYKM + " text not null," +
	                       VISIBILITYMI + " text not null," +
	                       MOBLIELINK + " text not null," + 
	                       DATE + " text not null," +
	                       DAY + " text not null," +
	                       TIME + " text not null" + 
	                       ")";
		String sql_forecasts = "CREATE TABLE " + TABLE_NAME_FORECASTS + "(" +
			                ID + " INTEGER PRIMARY KEY autoincrement," +
					        CITY_KEY + " text not null," +  
			                DAYNUM + " text not null," +
			                WEEKNUM + " text not null," + 
			                ICONNUM + " text not null," + 
			                ICONPHRASE + " text not null," +
			                HIGHC + " text not null," + 
			                HIGHF + " text not null," +
			                LOWC + " text not null," + 
			                LOWF + " text not null," +                
			                FOREMOBLIELINK + " text not null" + 
			                ")";
		database.execSQL(sql_current);//not db
		database.execSQL(sql_forecasts);
	}
	
    public CurrentConditions query(String cityKey){
    	String sql = "select * from " + TABLE_NAME_CURRENT + " where " + CITY_KEY + "=" + cityKey;
		Cursor cursor = db.rawQuery(sql, null);
		
		if(null != cursor && cursor.moveToFirst()){
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

    public Day query_forecasts(String key, String dayNum){
    	//ArrayList<Day> forecastsList = new ArrayList<Day>();
    	Day mDay = null;
    	String sql = "select * from " + TABLE_NAME_FORECASTS + " where " + CITY_KEY + " = " + key + " and " + DAYNUM + " = " + dayNum;
    	Cursor cursor = db.rawQuery(sql, null);
    	if(null != cursor && cursor.moveToFirst()){
			mDay = new Day();
			//String mKey = cursor.getString(cursor.getColumnIndex(WeatherDB.CITY_KEY));
    		String mWeek = cursor.getString(cursor.getColumnIndex(WeatherDB.WEEKNUM));
    		String mDayNum = cursor.getString(cursor.getColumnIndex(WeatherDB.DAYNUM));
    		String mIcon = cursor.getString(cursor.getColumnIndex(WeatherDB.ICONNUM));
    		String mIconPhrase = cursor.getString(cursor.getColumnIndex(WeatherDB.ICONPHRASE));
    		String mHighC = cursor.getString(cursor.getColumnIndex(WeatherDB.HIGHC));
    		String mHighF = cursor.getString(cursor.getColumnIndex(WeatherDB.HIGHF));
    		String mLowC = cursor.getString(cursor.getColumnIndex(WeatherDB.LOWC));
    		String mLowF = cursor.getString(cursor.getColumnIndex(WeatherDB.LOWF));
    		String mMobileLink = cursor.getString(cursor.getColumnIndex(WeatherDB.FOREMOBLIELINK));
    		
    		mDay.setKey(key);
    		mDay.setWeek(mWeek);
    		mDay.setDayNum(mDayNum);
    		mDay.setIconNumber(mIcon);
    		mDay.setIconPhrase(mIconPhrase);
    		mDay.setHighTempC(mHighC);mDay.setHighTempF(mHighF);
    		mDay.setLowTempC(mLowC);mDay.setLowTempF(mLowF);
    		mDay.setMobileLink(mMobileLink);
    	}
    	
    	return mDay;
    }
    
    public Cursor queryByLine(int line){
    	/*method1*/
    	String sql = "select * from " + TABLE_NAME_CURRENT + " limit " + line + ",1";
		return db.rawQuery(sql, null);
		/*method2:
		 * cursor.moveToPosition(position);
		 * */
	}
    
    public ArrayList<String> getSavedKeys(){
    	ArrayList<String> list = new ArrayList<String>();
    	String sql = "select " + CITY_KEY + " from " + TABLE_NAME_CURRENT;
    	Cursor cursor = db.rawQuery(sql, null);
    	if(null == cursor){
    		return null;
    	}
    	if(cursor.moveToFirst()){
    		while (!cursor.isAfterLast()){
    			list.add(cursor.getString(cursor.getColumnIndex(WeatherDB.CITY_KEY)));
    			cursor.moveToNext();
			} 
    	}
    	return list;
    }
    
    public int getCities(){
    	String sql = "select count(*) from " + TABLE_NAME_CURRENT;
    	Cursor cursor = db.rawQuery(sql, null);
    	cursor.moveToFirst();
    	return cursor.getInt(0);
    }
    
    public void update(CurrentConditions cc){
    	String sql = "update " + TABLE_NAME_CURRENT + " set " + 
                WEATHERTEXT + " = ?," + 
    			ICON + " = ?," +
                DAYTIME + " = ?," +
    			TEMPC + " = ?," +
    			TEMPF + " = ?," +
    			REALFEELC + " = ?," + 
    			REALFEELF  + " = ?," +
    			HUMIDITY  + " = ?," +
    			WINDSPEEDKM  + " = ?," +
    			WINDSPEEDMI  + " = ?," +
    			VISIBILITYKM  + " = ?," +
    			VISIBILITYMI  + " = ?," + 
    			MOBLIELINK + " = ?," +
    			DATE  + " = ?," +
    			DAY  + " = ?," + 
    			TIME + " = ?" +
    			" where " + 
    			CITY_KEY + " = ?";

    	db.execSQL(sql, new Object[]{cc.getWeathertext(), cc.getIcon(),cc.getDayTime(),
    			cc.getTemperatureC(), cc.getTemperatureF(),
    			cc.getRealfeelC(), cc.getRealfeelF(), cc.getHumidity(), cc.getWindspeedKM(), cc.getWindspeedMI(),
    			cc.getVisibilityKM(), cc.getVisibilityMI(), cc.getMobileLink(),
    			cc.getDate(), cc.getDay(), cc.getTime(), cc.getKey()});
    	
    }
    
    public void update_forecasts(Day mDay){
    	String sql = "update " + TABLE_NAME_FORECASTS + " set " + 
                WEEKNUM + " = ?," + 
    			ICONNUM + " = ?," +
                ICONPHRASE + " = ?," +
                HIGHC + " = ?," +
                HIGHF + " = ?," + 
    			LOWC + " = ?," +
                LOWF + " = ?," +    			
    			FOREMOBLIELINK + " = ?" +
    			" where " + 
    			CITY_KEY + " = ?" + " and " + DAYNUM + " = ?" ;

    	db.execSQL(sql, new Object[]{mDay.getWeek(), mDay.getIconNumber(), mDay.getIconPhrase(), mDay.getHighTempC(), mDay.getHighTempF(),
    			mDay.getLowTempC(), mDay.getLowTempF(), mDay.getMobileLink(), mDay.getKey(), mDay.getDayNum()});
    }
    
    public void insert(CurrentConditions cc){
    	String sql = "insert into " + TABLE_NAME_CURRENT + "(" + CITY_KEY + "," +
                         CITY_NAME + "," +
    			         WEATHERTEXT + "," +
                         ICON + "," +
    			         DAYTIME + "," +
                         TEMPC + "," +
    			         TEMPF + "," +
                         REALFEELC + "," +
    			         REALFEELF + "," +
                         HUMIDITY + "," +
    			         WINDSPEEDKM + "," +
                         WINDSPEEDMI + "," +
    			         VISIBILITYKM + "," +
                         VISIBILITYMI + "," +
    			         MOBLIELINK + "," +
                         DATE + "," +
    			         DAY + "," +
                         TIME +
                         ")" +
    			         " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    	db.execSQL(sql, new Object[]{cc.getKey(), cc.getName(), cc.getWeathertext(), 
    			cc.getIcon(), cc.getDayTime(), cc.getTemperatureC(), 
    			cc.getTemperatureF(), cc.getRealfeelC(), cc.getRealfeelF(), cc.getHumidity(), cc.getWindspeedKM(),
    			cc.getWindspeedMI(), cc.getVisibilityKM(), cc.getVisibilityMI(),
    			cc.getMobileLink(), cc.getDate(), cc.getDay(), cc.getTime()});
    	
    }
    
    public void insert_forecasts(Day mDay){
    	String sql = "insert into " + TABLE_NAME_FORECASTS + "(" + CITY_KEY + "," +
                         DAYNUM + "," +
                         WEEKNUM + "," +
    			         ICONNUM + "," +
                         ICONPHRASE + "," +
                         HIGHC + "," +
    			         HIGHF + "," +
                         LOWC + "," +
    			         LOWF + "," +
                         FOREMOBLIELINK +
                         ")" +
    			         " values(?,?,?,?,?,?,?,?,?,?)";
    	db.execSQL(sql, new Object[]{mDay.getKey(), mDay.getDayNum(), mDay.getWeek(), mDay.getIconNumber(), mDay.getIconPhrase(),
    			mDay.getHighTempC(), mDay.getHighTempF(), mDay.getLowTempC(), mDay.getLowTempF(), mDay.getMobileLink()});
    	
    }
    
    public void delete(CurrentConditions cc){
    	String sql = "delete from " + TABLE_NAME_CURRENT + " where " + CITY_KEY + " = ?";
    	db.execSQL(sql, new Object[]{cc.getKey()});
    }
    
    public void delete_forecasts(String key){
    	String sql = "delete from " + TABLE_NAME_FORECASTS + " where " + CITY_KEY + " = ?";
    	db.execSQL(sql, new Object[]{key});
    }
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exits " + TABLE_NAME_CURRENT;
		String sql_forecasts = "drop table if exits " + TABLE_NAME_FORECASTS;
		db.execSQL(sql);
		db.execSQL(sql_forecasts);
		onCreate(db);
	}

	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		db.close();
		super.close();
	}

}
