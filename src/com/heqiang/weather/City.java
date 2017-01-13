package com.heqiang.weather;

public class City {
	private String locationKey;
    private String cityName;
    private String englishName;
    private String state;
    private String updateTime;
    private String country;
    private String latitude;
    private String longitude;
    private boolean isAutoLocate;
    
    public void setLocationKey(String locationKey){
    	this.locationKey = locationKey;
    }
    
    public String getLocationKey(){
    	return locationKey;
    }
    
    public void setCityName(String cityName){
    	this.cityName = cityName;
    }
    
    public String getCityName(){
    	return cityName;
    }
    
    public void setEnglishName(String englishName){
    	this.englishName = englishName;
    }
    
    public String getEnglishName(){
    	return englishName;
    }
    
    public void setState(String state){
    	this.state = state;
    }
    
    public String getState(){
    	return state;
    }
    
    public void setUpdateTime(String updateTime){
    	this.updateTime = updateTime;
    }
    
    public String getUpdateTime(){
    	return updateTime;
    }
    
    public void setCountry(String country){
    	this.country = country;
    }
    
    public String getCountry(){
    	return country;
    }
    
    public void setLatitude(String latitude){
    	this.latitude = latitude;
    }
    
    public String getLatitude(){
    	return latitude;
    }
    
    public void setLongitude(String longitude){
    	this.longitude = longitude;
    }
    
    public String getLongitude(){
    	return longitude;
    }
    
    public void setAutoLocate(boolean isAutoLocate){
    	this.isAutoLocate = isAutoLocate;
    }
    
    public boolean getAutoLocate(){
    	return isAutoLocate;
    }
    

}
