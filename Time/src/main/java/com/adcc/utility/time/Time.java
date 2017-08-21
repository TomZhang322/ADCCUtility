package com.adcc.utility.time;

import com.adcc.utility.log.Log;
import com.google.common.base.Strings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 */
public class Time {
    public final static String yyyy_MM_dd = "yyyy-MM-dd";

    public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public final static String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public final static String yyyyMMddHH = "yyyyMMddHH";

    public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

    public static final String mmss = "mm:ss";

    public static final String HHmmss = "HHmmss";

    public static final String HH_mm_ss = "HH:mm:ss";

    public static final String ddHHmm = "ddHHmm";

    public final static long TIME_ZERO_TIMESTAMP = 0;

    public final static long TIME_TWENTY_FOUR_TIMESTAMP = 2359;

    public final static int SECOND_TO_MSEC = 1000;

    public final static String UTC_TIMEZONE = "UTC";

    /**
     * 日期时间转换为Date类型
     * @param dateTime
     * @param format
     * @return
     */
    public static Date formatDateTimeToDate(String dateTime,String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(dateTime);
            return date;
        } catch (Exception ex) {
            Log.error(Time.class.getName(), "formatStringToDate() error", ex);
            return null;
        }
    }

    /**
     * 日期时间转换为Date类型
     * @param dateTime
     * @param format
     * @return
     */
    public static Date formatDateTimeToUTCDate(String dateTime,String format){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
            int dstOffset = cal.get(Calendar.DST_OFFSET);
            cal.add(Calendar.MILLISECOND,-(zoneOffset+dstOffset));
            return cal.getTime();
        }catch (Exception ex){
            Log.error(Time.class.getName(),"formatDateTimeToDate() error",ex);
            return null;
        }
    }

    /**
     * 日期时间转换为String类型
     * @param dateTime
     * @param format
     * @return
     */
    public static String formatDateTimeToString(Date dateTime,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String strDateTime = sdf.format(dateTime);
        return strDateTime;
    }

    /**
     * 日期时间转换为String类型
     * @param dateTime
     * @param format
     * @return
     */
    public static String formatDateTimeToUTCString(Date dateTime,String format){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND,-(zoneOffset + dstOffset));
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String strDateTime = sdf.format(cal.getTime());
        return strDateTime;
    }

    /**
     * 获取当前UTC时间
     * */
    public static Date getCurUTCDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND,-(zoneOffset + dstOffset));
        return cal.getTime();
    }

    /**
     * 时间格式String类型转Date类型
     * @param str
     * @param formatStr
     * @return
     */
    public static Date strToDate(String str, String formatStr) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            date = format.parse(str);
        } catch (ParseException e) {
            System.out.println("日期格式转换出错!");
        }
        return date;
    }

    /**
     * 时间格式String类型转Date类型（str为本地时间字符串，如果targetTimeZoneStr为空，则默认转为本地时间，如果targetTimeZoneStr不为空，则转为该时区对应的时间）
     * @param str
     * @param formatStr
     * @param targetTimeZoneStr
     * @return
     */
    public static Date strToDate(String str, String formatStr, String targetTimeZoneStr) {
        Date date = null;
        if (Strings.isNullOrEmpty(targetTimeZoneStr)) {
            date = strToDate(str, formatStr);
        } else {
            Date sourceDate = strToDate(str, formatStr);
            TimeZone sourceTimeZone = TimeZone.getDefault();
            TimeZone targetTimeZone = TimeZone.getTimeZone(targetTimeZoneStr);

            Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
            date = new Date(targetTime);
        }
        return date;
    }

    /**
     * 时间格式Date类型转String类型
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(formatStr);
        return df.format(date);
    }

    /**
     * 时间格式Date类型转String类型（date为本地时间，如果targetTimeZoneStr为空，则默认转为本地时间字符串，如果targetTimeZoneStr不为空，则转为该时区对应的时间字符串）
     * @param date
     * @param formatStr
     * @param targetTimeZoneStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr, String targetTimeZoneStr) {
        String result = null;
        if (date != null) {
            if (Strings.isNullOrEmpty(targetTimeZoneStr)) {
                result = dateToStr(date, formatStr);
            } else {
                TimeZone sourceTimeZone = TimeZone.getDefault();
                TimeZone targetTimeZone = TimeZone.getTimeZone(targetTimeZoneStr);
                Long targetTime = date.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();

                result = dateToStr(new Date(targetTime), formatStr);
            }
        }
        return result;
    }

    /**
     * 获取目标时区的当前时间（如果targetTimeZoneStr为空，则获取系统默认时区的当前时间，如果targetTimeZoneStr不为空，则获取指定时区的当前时间）
     * @param targetTimeZoneStr
     * */
    public static Date getCurDate(String targetTimeZoneStr) {
        Date date = new Date();
        if (!Strings.isNullOrEmpty(targetTimeZoneStr)) {
            TimeZone sourceTimeZone = TimeZone.getDefault();
            TimeZone targetTimeZone = TimeZone.getTimeZone(targetTimeZoneStr);
            Long targetTime = date.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
            date = new Date(targetTime);
        }
        return date;
    }

    /**
     * 获取默认时区的当前时间字符串
     * @param formatStr
     * */
    public static String getCurDateStr(String formatStr) {
        return dateToStr(new Date(), formatStr);
    }

    /**
     * 获取目标时区的当前时间字符串（如果targetTimeZoneStr为空，则获取系统默认时区的当前时间字符串，如果targetTimeZoneStr不为空，则获取指定时区的当前时间字符串）
     * @param formatStr
     * @param targetTimeZoneStr
     * */
    public static String getCurDateStr(String formatStr, String targetTimeZoneStr) {
        return dateToStr(getCurDate(targetTimeZoneStr), formatStr);
    }

    /**
     * 时间换算
     * @param msec
     * @param formatStr
     * @return
     */
    public static Date getMsecToDate(long msec, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(msec);
        return strToDate(sdf.format(calendar.getTime()), formatStr);
    }

    /**
     * 时间处理
     * @param originDate
     *              初始时间
     * @param field
     *              单位
     * @param amount
     *              推算值
     * @return
     */
    public static Date estimationTime(Date originDate, int field, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(originDate);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 时间推算
     * @param msec
     * @param formatStr
     * @return
     */
    public static Date getDateBefore(long msec, String formatStr) {
        Date date = new Date(msec);
        return getDateBefore(date, formatStr);
    }

    /**
     * 时间推算
     * @param date
     * @param formatStr
     * @return
     */
    public static Date getDateBefore(Date date, String formatStr) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);
        String dayBeforeStr = new SimpleDateFormat(formatStr).format(c.getTime());
        return strToDate(dayBeforeStr, formatStr);
    }

    /**
     * 获取星期几（取值1,2,3,4,5,6,7）
     * @param date
     * @return
     */
    public synchronized static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return w == 0 ? 7 : w ;
    }

    /**
     * 比较本地时间(-1:第一个时间小于第二个时间,0:第一个时间等于第二个时间,1:第一个时间大于第二个时间)
     * @param dateTime1
     * @param dateTime2
     * @return
     */
    public static int compare(Date dateTime1,Date dateTime2) throws ParseException {
        if(dateTime1.after(dateTime2)){
            return 1;
        }else if(dateTime1.before(dateTime2)){
            return -1;
        }else{
            return 0;
        }
    }

    /**
     * 比较本地时间(-1:第一个时间小于第二个时间,0:第一个时间等于第二个时间,1:第一个时间大于第二个时间)
     * @param dateTime1
     * @param dateTime2
     * @param format
     * @return
     */
    public static int compare(String dateTime1,String dateTime2,String format) throws ParseException {
        Date date1 = new SimpleDateFormat(format).parse(dateTime1);
        Date date2 = new SimpleDateFormat(format).parse(dateTime2);
        if(date1.after(date2)){
            return 1;
        }else if(date1.before(date2)){
            return -1;
        }else{
            return 0;
        }
    }
}
