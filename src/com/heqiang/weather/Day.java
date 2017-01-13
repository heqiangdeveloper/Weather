package com.heqiang.weather;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable{
	private String dayNum;
	private String key;
	private String week;
	private String highTempC,highTempF;
	private String lowTempC,lowTempF;
	private String iconNumber;
	private String mobileLink;
	private String iconPhrase;
	
	public String toString(){
		return "week:" + week + ",highTempC:" + highTempC + ",lowTempC:" + lowTempC;
	}
	
	public void setDayNum(String dayNum){
		this.dayNum = dayNum;
	}
	
	public String getDayNum(){
		return dayNum;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public String getKey(){
		return key;
	}
	
	public void setWeek(String week){
		this.week = week;
	}
	
	public String getWeek(){
		return week;
	}
	
	public void setHighTempC(String highTempC){
		this.highTempC = highTempC;
	}
	
	public void setHighTempF(String highTempF){
		this.highTempF = highTempF;
	}
	
	public String getHighTempC(){
		return highTempC;
	}
	
	public String getHighTempF(){
		return highTempF;
	}
	
	public void setLowTempC(String lowTempC){
		this.lowTempC = lowTempC;
	}
	
	public void setLowTempF(String lowTempF){
		this.lowTempF = lowTempF;
	}
	
	public String getLowTempC(){
		return lowTempC;
	}
	
	public String getLowTempF(){
		return lowTempF;
	}
	
	public void setIconNumber(String iconNumber){
		this.iconNumber = iconNumber;
	}
	
	public String getIconNumber(){
		return iconNumber;
	}
	
	public void setMobileLink(String mobileLink){
		this.mobileLink = mobileLink;
	}
	
	public String getMobileLink(){
		return mobileLink;
	}
	
	public void setIconPhrase(String iconPhrase){
		this.iconPhrase = iconPhrase;
	}
	
	public String getIconPhrase(){
		return iconPhrase;
	}

	public static final Parcelable.Creator<Day> CREATOR = new Creator<Day>() {

		public Day createFromParcel(Parcel source) {
			Day day = new Day();
			day.setDayNum(source.readString());
			day.setKey(source.readString());
			day.setWeek(source.readString());
			day.setHighTempC(source.readString());day.setHighTempF(source.readString());
			day.setLowTempC(source.readString());day.setLowTempF(source.readString());
			day.setIconNumber(source.readString());
			day.setMobileLink(source.readString());
			day.setIconPhrase(source.readString());
			return day;
		}

		public Day[] newArray(int size) {
			return new Day[size];
		}
	};
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dayNum);
		dest.writeString(key);
		dest.writeString(week);
		dest.writeString(highTempC);dest.writeString(highTempF);
		dest.writeString(lowTempC);dest.writeString(lowTempF);
		dest.writeString(iconNumber);	
		dest.writeString(mobileLink);
		dest.writeString(iconPhrase);
	}

}
