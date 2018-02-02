package com.portablesalescounterapp.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author pocholomia
 * @since 13/09/2016
 */
public class DateTimeUtils {

    public static final String FULL_23_HR_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_ONLY = "MMM dd, yyyy";
    public static final String DATE_NUM_ONLY = "MM.dd.yyyy";


    public static String parseDoubleTL(Double value){


        String g = NumberFormat.getCurrencyInstance().format(Double.parseDouble(""+value));


            return NumberFormat.getNumberInstance(Locale.US).format(value);

       // DecimalFormat df =  new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US));
       // Double doublePrice = 0.0;
       /* try {
            doublePrice =  df.parse(value).doubleValue();
            Log.d("convert>>> ", doublePrice+"");
        } catch (ParseException e) {
            Log.d("convert>>> ", e+"");
        }*/
       // return String.valueOf(g);
    }


    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static String getCurrentTimeStamp2(){
        try {

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            return ts;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static String getTimeOnly(String dateString) {
        return convertDateToString("hh:mm a", convertTransactionStringDate(dateString, FULL_23_HR_DATE));
    }

    public static String getShortDateTime(String dateString) {
        return convertDateToString("MMM dd, yyyy \n h:mm a", convertTransactionStringDate(dateString, FULL_23_HR_DATE));
    }


    public static String getShortBirthDateOnly(String dateString) {
        return convertDateToString(DATE_ONLY, convertTransactionStringDate(dateString, "yyyy-MM-dd"));
    }

    public static Calendar convertTransactionStringDate(String dateString, String format) {
        Date date = convertStringDate(dateString, format);
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date convertStringDate(String dateString, String format) {
        Date date = null;
        try {
            date = getSimpleDateFormat(format).parse(dateString);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("dateString: ", dateString + "");
            e.printStackTrace();
        }
        return date;
    }

    public static String convertDateToString(String format, Calendar calendar) {
        if (calendar == null) return "";
        return getSimpleDateFormat(format).format(calendar.getTime());
    }

    public static SimpleDateFormat getSimpleDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH);
    }

    public static String getDateOnlyToday() {
        return convertDateToString("yyyy-MM-dd", Calendar.getInstance());
    }

    public static String convertTime(String time)
    {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            final Date dateObj = sdf.parse(time);
            //DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
           return new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String toReadable(String dateToConvert){
        String convertedDate;
        //String[] arr = dateToConvert.split(" ");
        DateFormat targetFormat = new SimpleDateFormat(FULL_23_HR_DATE, Locale.US);
        Date date = null;
        try {
            date = targetFormat.parse(dateToConvert);


        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, yyyy", Locale.US);
        convertedDate = formatter.format(date);
        return convertedDate.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
