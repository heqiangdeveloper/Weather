package com.heqiang.weather;

import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.net.Uri;

public class IconBackgroundUtil {
	static Uri uriClear = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_clear);
    static Uri uriNightCloundy = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_night_clundy);
    static Uri uriCloundy = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_cloundy);
    static Uri uriFog = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_fog);
    static Uri uriFrost = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_frost);
    static Uri uriRain = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_rain);
    static Uri uriSnow = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_snow);
    static Uri uriStrom = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_storm);
    static Uri uriSunny = Uri.parse("android.resource://com.heqiang.weather/" + R.raw.am_sunny);
    
    public static Map<Integer, Uri> backgroundUriArray = new HashMap<Integer, Uri>() {
        {
            put(1, uriSunny);
            put(2, uriSunny);
            put(4, uriSunny);
            put(5, uriSunny);
            put(14, uriSunny);
            put(21, uriSunny);
            put(30, uriSunny);

            put(33, uriClear);
            put(34, uriClear);
            put(36, uriClear);
            put(37, uriClear);

            put(35, uriNightCloundy);
            put(38, uriNightCloundy);

            put(3, uriCloundy);
            put(6, uriCloundy);
            put(7, uriCloundy);
            put(8, uriCloundy);
            put(13, uriCloundy);
            put(16, uriCloundy);
            put(17, uriCloundy);
            put(20, uriCloundy);
            put(23, uriCloundy);
            put(32, uriCloundy);

            put(11, uriFog);

            put(24, uriFrost);
            put(31, uriFrost);

            put(12, uriRain);
            put(18, uriRain);
            put(25, uriRain);
            put(26, uriRain);
            put(39, uriRain);
            put(40, uriRain);

            put(19, uriSnow);
            put(22, uriSnow);
            put(29, uriSnow);
            put(43, uriSnow);
            put(44, uriSnow);

            put(15, uriStrom);
            put(41, uriStrom);
            put(42, uriStrom);
        }
    };  
    
    public static Map<Integer, Integer> backgroundImgArray = new HashMap<Integer, Integer>() {
        {
            put(1, R.drawable.bg_sunny);
            put(2, R.drawable.bg_sunny);
            put(4, R.drawable.bg_sunny);
            put(5, R.drawable.bg_sunny);
            put(14, R.drawable.bg_sunny);
            put(21, R.drawable.bg_sunny);
            put(30, R.drawable.bg_sunny);

            put(33, R.drawable.bg_clear);
            put(34, R.drawable.bg_clear);
            put(36, R.drawable.bg_clear);
            put(37, R.drawable.bg_clear);

            put(35, R.drawable.bg_night_cloudy);
            put(38, R.drawable.bg_night_cloudy);

            put(3, R.drawable.bg_cloudy);
            put(6, R.drawable.bg_cloudy);
            put(7, R.drawable.bg_cloudy);
            put(8, R.drawable.bg_cloudy);
            put(13, R.drawable.bg_cloudy);
            put(16, R.drawable.bg_cloudy);
            put(17, R.drawable.bg_cloudy);
            put(20, R.drawable.bg_cloudy);
            put(23, R.drawable.bg_cloudy);
            put(32, R.drawable.bg_cloudy);

            put(11, R.drawable.bg_fog);

            put(24, R.drawable.bg_frost);
            put(31, R.drawable.bg_frost);

            put(12, R.drawable.bg_rainy);
            put(18, R.drawable.bg_rainy);
            put(25, R.drawable.bg_rainy);
            put(26, R.drawable.bg_rainy);
            put(39, R.drawable.bg_rainy);
            put(40, R.drawable.bg_rainy);

            put(19, R.drawable.bg_snow);
            put(22, R.drawable.bg_snow);
            put(29, R.drawable.bg_snow);
            put(43, R.drawable.bg_snow);
            put(44, R.drawable.bg_snow);

            put(15, R.drawable.bg_storm);
            put(41, R.drawable.bg_storm);
            put(42, R.drawable.bg_storm);
        }
    };
    
    public static Map<Integer, Integer> forecastImgArray = new HashMap<Integer, Integer>(){
    	{
    		put(1, R.drawable.ic_sunny);
            put(2, R.drawable.ic_mostly_sunny);
            put(3, R.drawable.ic_partly_sunny);
            put(4, R.drawable.ic_mostly_sunny);
            put(5, R.drawable.ic_hazy_sunshine);
            put(6, R.drawable.ic_mostly_cloudy);
            put(7, R.drawable.ic_cloudy);
            put(8, R.drawable.ic_dreary);
            put(11, R.drawable.ic_fog);
            put(12, R.drawable.ic_showers);
            put(13, R.drawable.ic_mostly_cloudy_w_showers);
            put(14, R.drawable.ic_partly_sunny_w_showers);
            put(15, R.drawable.ic_t_storms);
            put(16, R.drawable.ic_mostly_cloudy_w_t_storms);
            put(17, R.drawable.ic_partly_sunny_w_t_storms);
            put(18, R.drawable.ic_rain);
            put(19, R.drawable.ic_flurries);
            put(20, R.drawable.ic_snow);
            put(21, R.drawable.ic_snow);
            put(22, R.drawable.ic_snow);
            put(23, R.drawable.ic_mostly_cloudy_w_snow);
            put(24, R.drawable.ic_ice);
            put(25, R.drawable.ic_sleet);
            put(26, R.drawable.ic_freezing_rain);
            put(29, R.drawable.ic_rain_snow);
            put(30, R.drawable.ic_hot);
            put(31, R.drawable.ic_cold);
            put(32, R.drawable.ic_windy);
            put(33, R.drawable.ic_clear);
            put(34, R.drawable.ic_partly_cloudy);
            put(35, R.drawable.ic_partly_cloudy);
            put(36, R.drawable.ic_intermittent_clouds);
            put(37, R.drawable.ic_hazy_moonlight);
            put(38, R.drawable.ic_cloudy);
            put(39, R.drawable.ic_partly_cloudy_w_showers);
            put(40, R.drawable.ic_mostly_cloudy_w_showers);
            put(41, R.drawable.ic_partly_cloudy_w_t_storms);
            put(42, R.drawable.ic_mostly_cloudy_w_t_storms);
            put(43, R.drawable.ic_mostly_cloudy_w_snow);
            put(44, R.drawable.ic_mostly_cloudy_w_snow);
    	}
    };
    public static Map<Integer, Integer> smallWidgetWhiteIconArray = new HashMap<Integer, Integer>() {
        {
            put(1, R.drawable.ic_thin_white_sunny);
            put(2, R.drawable.ic_thin_white_mostly_sunny);
            put(3, R.drawable.ic_thin_white_partly_sunny);
            put(4, R.drawable.ic_thin_white_intermittent_clouds);
            put(5, R.drawable.ic_thin_white_hazy_sunshine);
            put(6, R.drawable.ic_thin_white_mostly_cloudy);
            put(7, R.drawable.ic_thin_white_cloudy);
            put(8, R.drawable.ic_thin_white_dreary);
            put(11, R.drawable.ic_thin_white_fog);
            put(12, R.drawable.ic_thin_white_showers);
            put(13, R.drawable.ic_thin_white_mostly_cloudy_w_showers);
            put(14, R.drawable.ic_thin_white_partly_sunny_w_showers);
            put(15, R.drawable.ic_thin_white_tstorms);
            put(16, R.drawable.ic_thin_white_mostly_cloudy_w_tstorms);
            put(17, R.drawable.ic_thin_white_tstorms);
            put(18, R.drawable.ic_thin_white_rain);
            put(19, R.drawable.ic_thin_white_flurries);
            put(20, R.drawable.ic_thin_white_mostly_cloudy_w_flurries);
            put(21, R.drawable.ic_thin_white_mostly_cloudy_w_flurries);
            put(22, R.drawable.ic_thin_white_snow);
            put(23, R.drawable.ic_thin_white_mostly_cloudy_w_snow);
            put(24, R.drawable.ic_thin_white_ice);
            put(25, R.drawable.ic_thin_white_sleet);
            put(26, R.drawable.ic_thin_white_freezing_rain);
            put(29, R.drawable.ic_thin_white_rain_snow);
            put(30, R.drawable.ic_thin_white_hot);
            put(31, R.drawable.ic_thin_white_cold);
            put(32, R.drawable.ic_thin_white_windy);
            put(33, R.drawable.ic_thin_white_clear);
            put(34, R.drawable.ic_thin_white_mostly_clear);
            put(35, R.drawable.ic_thin_white_partly_cloudy_night);
            put(36, R.drawable.ic_thin_white_intermittent_clouds_night);
            put(37, R.drawable.ic_thin_white_hazy_moonlight_night);
            put(38, R.drawable.ic_thin_white_mostly_cloudy_night);
            put(39, R.drawable.ic_thin_white_partly_cloudy_w_showers_night);
            put(40, R.drawable.ic_thin_white_mostly_cloudy_w_showers_night);
            put(41, R.drawable.ic_thin_white_partly_cloudy_w_tstorms_night);
            put(42, R.drawable.ic_thin_white_mostly_cloudy_w_tstorms_night);
            put(43, R.drawable.ic_thin_white_mostly_cloudy_w_flurries);
            put(44, R.drawable.ic_thin_white_mostly_cloudy_w_snow_night);
        }
    };
    public static Integer getBackgroundImg(Integer i){
    	return backgroundImgArray.get(i);
    }
    
    public static Uri getBackgroundUri(Integer i){
    	return backgroundUriArray.get(i);
    }
    
    public static Integer getForecastImg(Integer i){
    	return forecastImgArray.get(i);
    }
   
    
    public static Integer getSmallWidgetWhiteIcon(Integer i){
    	return smallWidgetWhiteIconArray.get(i);
    }
}
