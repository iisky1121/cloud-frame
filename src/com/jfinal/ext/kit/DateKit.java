package com.jfinal.ext.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateKit.
 */
public class DateKit {
	
	public final static String dateFormat = "yyyy-MM-dd";
	public final static String dateNumberFormat = "yyyyMMdd";
	public final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	public final static String dateTimeNumberFormat = "yyyyMMddHHmmss";
	public final static String timeFormat = "HH:mm:ss";
	public final static String timeNumberFormat = "HHmmss";
	
	public static String toDateStr(Date date) {
		return toStr(date, DateKit.dateFormat);
	}
	
	public static String toDateTimeStr(Date date) {
		return toStr(date, DateKit.dateTimeFormat);
	}
	
	public static String toTimeStr(Date date) {
		return toStr(date, DateKit.timeFormat);
	}
	
	public static String getDateStr() {
		return toStr(new Date(), DateKit.dateFormat);
	}
	
	public static String getDateTimeStr() {
		return toStr(new Date(), DateKit.dateTimeFormat);
	}
	
	public static String getTimeStr() {
		return toStr(new Date(), DateKit.timeFormat);
	}
	
	public static String getDateNumberStr() {
		return toStr(new Date(), DateKit.dateNumberFormat);
	}
	
	public static String getDateTimeNumberStr() {
		return toStr(new Date(), DateKit.dateTimeNumberFormat);
	}
	
	public static String getTimeNumberStr() {
		return toStr(new Date(), DateKit.timeNumberFormat);
	}
	
	public static String toStr(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static Date toDate(String dateStr) {
		return toDate(dateStr, dateTimeFormat);
	}
	public static Date toDate(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date addSecond(Date date, int addNumber){
		return add(date, Calendar.SECOND, addNumber);
	}
	
	public static Date addMinute(Date date, int addNumber){
		return add(date, Calendar.MINUTE, addNumber);
	}
	
	public static Date addHour(Date date, int addNumber){
		return add(date, Calendar.HOUR, addNumber);
	}
	
	public static Date addDate(Date date, int addNumber){
		return add(date, Calendar.DATE, addNumber);
	}
	
	public static Date addMonth(Date date, int addNumber){
		return add(date, Calendar.MONTH, addNumber);
	}
	
	public static Date addYear(Date date, int addNumber){
		return add(date, Calendar.YEAR, addNumber);
	}
	/**
	 * 
	 * @param date
	 * @param addType 例如:Calendar.DATE,Calendar.MONDAY...
	 * @param addNumber 加为正数，减为负数
	 * @return
	 */
	public static Date add(Date date, int addType, int addNumber){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(addType, addNumber);
		return calendar.getTime();
	}

	/**
	 * 获取当前时间的时间戳秒
	 * @return
	 */
	public static int unixTimestamp(){
		return timeMillisToSecond(System.currentTimeMillis());
	}

	/**
	 * 获取指定时间的时间戳秒
	 * @param date
	 * @return
	 */
	public static int getUnixTimestamp(Date date){
		return timeMillisToSecond(date.getTime());
	}

	public static int getUnixTimestamp(String dateStr){
		return getUnixTimestamp(toDate(dateStr));
	}

	/**
	 * 时间戳long转int
	 * @param timeMillis
	 * @return
	 */
	public static int timeMillisToSecond(long timeMillis){
		return (int)(timeMillis/1000);
	}
}
