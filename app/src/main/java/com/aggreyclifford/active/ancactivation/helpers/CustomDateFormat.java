package com.aggreyclifford.active.ancactivation.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by getcore03 on 6/23/2016.
 */
public class CustomDateFormat {

    private static Calendar c = Calendar.getInstance();
    private static  SimpleDateFormat dateFormat;

    public static String getFormattedDate() {
        dateFormat = new SimpleDateFormat();
        return dateFormat.format(c.getTime());

    }

    public static String getFormattedTime() {
        dateFormat  = new SimpleDateFormat();
        String formattedTime = dateFormat.format(c.getTime());
        return formattedTime;

    }

    public static String getFormattedTime(String formatType) {
        dateFormat = new SimpleDateFormat(formatType);
        String formattedTime = dateFormat.format(c.getTime());
        return formattedTime;

    }

    public static String getFormattedDate(String formatType) {
        dateFormat = new SimpleDateFormat("dd-MM-yyy");
        String formattedDate = dateFormat.format(c.getTime());
        return formattedDate;

    }

    public static String getFormattedDateAndTime() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(c.getTime());
        return formattedDate;

    }

    public  static String convertStringToDate(String originalString){

        Date date = null;
        String dateString = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").parse(originalString);
            dateString = new SimpleDateFormat("H:mm a").format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
         // 9:00
        return dateString;
    }

    public static String  getWeek(String dateString) throws ParseException {
        Date todayDate = dateFormat.parse(dateString);
        c.setTime(todayDate);
        c.add(Calendar.DAY_OF_YEAR, -7);
        Date newDate = c.getTime();

        String date = dateFormat.format(newDate);
        return  date;
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    //extract day name from a date string
    public static String convertDateIntoDay(String input_date){

        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = inFormat.parse(input_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = outFormat.format(date);

        return dayOfTheWeek;
    }
}
