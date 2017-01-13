package com.heqiang.weather;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CurrentConditions implements Serializable{
	private String temperatureC,temperatureF;
	private String weathertext;
	private String realfeelC,realfeelF;
	private String humidity;
	private String windspeedKM,windspeedMI;
	private String visibilityKM,visibilityMI;
	private String hightemp;
	private String lowtemp;
	private String direction;
	private String name;
	private String key;
	private String icon;
	private String dayTime;
	private String mobileLink;
	private String date;
	private String day;
	private String time;
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "name:" + name + "," + "temperature:" + temperatureC + "," + "weathertext:" + weathertext + "," +
				"realfeel:" + realfeelC + "," + "humidity:" + humidity + "," + "windspeed:" + windspeedKM + "," +
		           "visibility:" + visibilityKM + "," + "direction:" + direction ;
	}
	
	public void setDate(String date){
		this.date = date; 
	}
	
	public String getDate(){
		return date;
	}
	
	public void setDay(String day){
		this.day = day; 
	}
	
	public String getDay(){
		return day;
	}
	
	public void setTime(String time){
		this.time = time; 
	}
	
	public String getTime(){
		return time;
	}
	
	public void setMobileLink(String mobileLink){
		this.mobileLink = mobileLink; 
	}
	
	public String getMobileLink(){
		return mobileLink;
	}
	
	public void setDayTime(String dayTime){
		this.dayTime = dayTime; 
	}
	
	public String getDayTime(){
		return dayTime;
	}
	
	public void setIcon(String icon){
		this.icon = icon; 
	}
	
	public String getIcon(){
		return icon;
	}
	
	public void setKey(String key){
		this.key = key; 
	}
	
	public String getKey(){
		return key;
	}
	
	public void setName(String name){
		this.name = name; 
	}
	
	public String getName(){
		return name;
	}

	public void setTemperatureC(String temperatureC){
		this.temperatureC = temperatureC;
	}
	
	public String getTemperatureC(){
		return temperatureC;
	}
	
	public void setTemperatureF(String temperatureF){
		this.temperatureF = temperatureF;
	}
	
	public String getTemperatureF(){
		return temperatureF;
	}
	
	public void setWeathertext(String weathertext){
		this.weathertext = weathertext;
	}
	
	public String getWeathertext(){
		return weathertext;
	}
	
	public void setRealfeelC(String realfeelC){
		this.realfeelC = realfeelC;
	}
	
	public String getRealfeelC(){
		return realfeelC;
	}
	
	public void setRealfeelF(String realfeelF){
		this.realfeelF = realfeelF;
	}
	
	public String getRealfeelF(){
		return realfeelF;
	}
	
	public void setHumidity(String humidity){
		this.humidity = humidity;
	}
	
	public String getHumidity(){
		return humidity;
	}
	
	public void setWindspeedKM(String windspeedKM){
		this.windspeedKM = windspeedKM;
	}
	
	public String getWindspeedKM(){
		return windspeedKM;
	}
	
	public void setWindspeedMI(String windspeedMI){
		this.windspeedMI = windspeedMI;
	}
	
	public String getWindspeedMI(){
		return windspeedMI;
	}
	
	public void setVisibilityKM(String visibilityKM){
		this.visibilityKM = visibilityKM;
	}
	
	public String getVisibilityKM(){
		return visibilityKM;
	}
	
	public void setVisibilityMI(String visibilityMI){
		this.visibilityMI = visibilityMI;
	}
	
	public String getVisibilityMI(){
		return visibilityMI;
	}
	
	public void setHightemp(String hightemp){
		this.hightemp = hightemp;
	}
	
	public String getHightemp(){
		return hightemp;
	}
	
	public void setLowtemp(String lowtemp){
		this.lowtemp = lowtemp;
	}
	
	public String getLowtemp(){
		return lowtemp;
	}
	
	public void setDirection(String direction){
		this.direction = direction;
	}
	
	public String getDirection() {
		return direction;
	}

}
