package com.bookkeep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bookkeep.Core.Library;

import java.util.Calendar;

/**
 * Created by Abdullah on 28/06/2016.
 */
public class Utilities {

    public static void setAlarmIfRequired(Context context) {

        Intent notificationIntent = new Intent(context, NotificationAlarmReceiver.class);
        final PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, notificationIntent,
                        PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent == null) {
            // There is no alarm set for same
            //Set Notifications AlarmManager
            AlarmManager AM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 18); //Fire Alarm at 18:00
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND,0);
            AM.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY , pending); //12 Hour Interval
        }
    }

    public static Library loadLibraryFromPref( Context context) {
        //If Library is available in SharedPref, Import it, otherwise Create a new Library
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson =  Converters.registerLocalDate(new GsonBuilder()).create();   //Convert To JSON to store in SharedPref, using a library to parse jodatime (joda-time-serializer)
        String json = mPrefs.getString("Library", "");
        Library tempLibrary = gson.fromJson(json, Library.class);
        return tempLibrary;
    }

    public static void saveLibraryToPref ( Context context , Library myLibrary) {

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson =  Converters.registerLocalDate(new GsonBuilder()).create();   //Convert To JSON to store in SharedPref, using a library to parse jodatime (joda-time-serializer)
        String json = gson.toJson(myLibrary); //
        prefsEditor.putString("Library", json);
        prefsEditor.apply();

    }

}



