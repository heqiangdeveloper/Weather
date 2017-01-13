package com.heqiang.weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

public class ResponseUtil {
	
	private List<City> cityList;
	private CurrentConditions current;
	private ArrayList<Day> dayList;
	private Context context;
	
	public List<City> getCityListFromJSON(JSONArray jsonArray){
		cityList = new ArrayList<City>();
		if (jsonArray != null && jsonArray.length() != 0) {
			parseCityListJSONArray(jsonArray);
		}else {
			return new ArrayList<City>();//or:return null;
		}
		return cityList;
	}
	
	public City getCityInfoFromGeoJsonArray(JSONObject jsonObject){
		City mCity = new City();
		if(null != jsonObject && jsonObject.length() != 0){
			try {
				String key =  jsonObject.getString("Key");
				String name = jsonObject.getString("LocalizedName");
				mCity.setCityName(name);
				mCity.setLocationKey(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
		return mCity;
	}
	
	public CurrentConditions getCurrentConditionsFromJSON(Context mContext, JSONArray jsonArray){
		context = mContext;
		current = new CurrentConditions();
		if (jsonArray != null && jsonArray.length() != 0) {
			//parseCurrentConditionsFromJSON(jsonObject);
			current = parseCurrentConditionsFromJSONArray(jsonArray);
		}else {
			current = null;
		}
		return current;
	}
	
	public ArrayList<Day> getForecastFromJSON(Context c, JSONObject jsonObject){
		context = c;
		dayList = new ArrayList<Day>();
		if (jsonObject != null && jsonObject.length() != 0) {
			parseDayListJSONObject(jsonObject);			
		}else {
			return new ArrayList<Day>();//or:return null;
		}
		return dayList;
	}
	
	/*public void parseDayListJSONArray(JSONArray jsonArray){		
	    int len = jsonArray.length();
	    JSONObject jsonObject;
		for(int i = 0; i < len; i++){
			try {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null && jsonObject.length() != 0) {
					parseDayListJSONObject(jsonObject);					
				}else {
					//
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}	
		
	}*/
	
	public void parseCityListJSONArray(JSONArray jsonArray){		
	    int len = jsonArray.length();
	    JSONObject jsonObject;
		for(int i = 0; i < len; i++){
			try {
				jsonObject = jsonArray.getJSONObject(i);
				if (jsonObject != null && jsonObject.length() != 0) {
					parseCityListJSONObject(jsonObject);					
				}else {
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	/*public void parseCurrentConditionsFromJSONArray(JSONArray jsonArray){	   
		if (jsonObject != null && jsonObject.length() != 0) {
			parseCurrentConditionsJSONObject(jsonObject);					
		}else {
			current = null;
		}
	}*/
	
	public void parseCityListJSONObject(JSONObject jsonObject){
		try {
			JSONObject countryObj = jsonObject.getJSONObject("Country");
			JSONObject administrativeAreaObj = jsonObject.getJSONObject("AdministrativeArea");
			
			City city = new City();
			city.setCountry(countryObj.getString("LocalizedName"));
			city.setState(administrativeAreaObj.getString("LocalizedName"));
			city.setCityName(jsonObject.getString("LocalizedName"));
			//city.setLatitude(jsonObject.getString("latitude"));
			//city.setLongitude(jsonObject.getString("longitude"));
			city.setLocationKey(jsonObject.getString("Key"));
			if (jsonObject.has("EnglishName")) {
				city.setEnglishName(jsonObject.getString("EnglishName"));
			}
			city.setUpdateTime("");
			
			cityList.add(city);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseDayListJSONObject(JSONObject jsonObject){
		JSONArray jsonArray = null;
		try {
			jsonArray = jsonObject.getJSONArray("DailyForecasts");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if(null != jsonArray){
			for(int i = 1; i < jsonArray.length(); i++){
				try {
					jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject != null && jsonObject.length() != 0) {
						parseDayListFromJSONObject(jsonObject);					
					}else {
						//
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public void parseDayListFromJSONObject(JSONObject jsonObject){
		Day mDay = new Day();
		try {
			String dateAndTime = jsonObject.getString("Date");
			String date = dateAndTime.substring(0,10);
			String day = getWeek(date);
			
			String iconNum = String.valueOf(jsonObject.getJSONObject("Day").getInt("Icon"));
			String iconPhrase = jsonObject.getJSONObject("Day").getString("IconPhrase");
			
			String highC = String.valueOf(jsonObject.getJSONObject("Temperature").getJSONObject("Maximum").getDouble("Value"));
			String highF = String.valueOf(convertCToF(Float.parseFloat(highC)));
			String lowC = String.valueOf(jsonObject.getJSONObject("Temperature").getJSONObject("Minimum").getDouble("Value"));
			String lowF = String.valueOf(convertCToF(Float.parseFloat(lowC)));
			String mobileLink = jsonObject.getString("MobileLink");
			
			mDay.setWeek(day);
			mDay.setIconNumber(iconNum);
			mDay.setHighTempC(highC);
			mDay.setHighTempF(highF);
			mDay.setLowTempC(lowC);
			mDay.setLowTempF(lowF);	
			mDay.setMobileLink(mobileLink);
			mDay.setIconPhrase(iconPhrase);
			dayList.add(mDay);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float convertCToF(float c){
		return (float) (c*9.0/5 + 32);
	}
	
	public CurrentConditions parseCurrentConditionsFromJSONArray(JSONArray jsonArray){
		JSONObject jsonObject = null;
		try {
			jsonObject = jsonArray.getJSONObject(0);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CurrentConditions currentConditions = new CurrentConditions();
		if(null != jsonObject && jsonObject.length() != 0){
			try {
				String temperatureC = String.valueOf(jsonObject.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value"));
				String temperatureF = String.valueOf(jsonObject.getJSONObject("Temperature").getJSONObject("Imperial").getDouble("Value"));
				String weathertext = jsonObject.getString("WeatherText");
				String realfeelC = String.valueOf(jsonObject.getJSONObject("RealFeelTemperature").getJSONObject("Metric").getDouble("Value"));
				String realfeelF = String.valueOf(jsonObject.getJSONObject("RealFeelTemperature").getJSONObject("Imperial").getDouble("Value"));
				String humidity = String.valueOf(jsonObject.getDouble("RelativeHumidity"));
				String windspeedKM = String.valueOf(jsonObject.getJSONObject("Wind").getJSONObject("Speed").getJSONObject("Metric").getDouble("Value"));
				String windspeedMI = String.valueOf(jsonObject.getJSONObject("Wind").getJSONObject("Speed").getJSONObject("Imperial").getDouble("Value"));
				String visibilityKM = String.valueOf(jsonObject.getJSONObject("Visibility").getJSONObject("Metric").getDouble("Value"));
				String visibilityMI = String.valueOf(jsonObject.getJSONObject("Visibility").getJSONObject("Imperial").getDouble("Value"));
				String direction = jsonObject.getJSONObject("Wind").getJSONObject("Direction").getString("Localized");
				String icon = String.valueOf(jsonObject.getInt("WeatherIcon"));
				String dayTime = String.valueOf(jsonObject.getBoolean("IsDayTime")); 
				String mobileLink = jsonObject.getString("MobileLink");
				String dateAndTime = jsonObject.getString("LocalObservationDateTime");//like 2016-12-30T20:33:00+08:00
				String date = dateAndTime.substring(0,10);   
				String day = getWeek(date);
				String time = dateAndTime.substring(11);		

				currentConditions.setTemperatureC(temperatureC); currentConditions.setTemperatureF(temperatureF);
				currentConditions.setWeathertext(weathertext);
				currentConditions.setRealfeelC(realfeelC); currentConditions.setRealfeelF(realfeelF);
				currentConditions.setHumidity(humidity);
				currentConditions.setWindspeedKM(windspeedKM); currentConditions.setWindspeedMI(windspeedMI);
				currentConditions.setVisibilityKM(visibilityKM); currentConditions.setVisibilityMI(visibilityMI);
				currentConditions.setDirection(direction);
				currentConditions.setIcon(icon);
				currentConditions.setDayTime(dayTime);
				currentConditions.setMobileLink(mobileLink);
				currentConditions.setDate(date);
				currentConditions.setDay(day);
				currentConditions.setTime(time);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return currentConditions;
	}

	public City getCityData(JSONArray jsonArray){
		return null;
	}

	private String getWeek(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] str = date.split("-");

        int year = Integer.parseInt(str[0]);
        int month = Integer.parseInt(str[1]);
        int day = Integer.parseInt(str[2]);
        calendar.set(year, month - 1, day);
        int number = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //String[] weekStr = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",};
        String[] weekStr = { getWeekday(R.string.sunday), getWeekday(R.string.monday), getWeekday(R.string.tuesday),
        		getWeekday(R.string.wednesday), getWeekday(R.string.thursday), getWeekday(R.string.friday),
        		getWeekday(R.string.saturday)};
        return weekStr[number];
    }
	
	public String getWeekday(int i){
		if(null != context){
			return context.getResources().getString(i);			
		}else {
			return "unknown";
		}
	}
}
