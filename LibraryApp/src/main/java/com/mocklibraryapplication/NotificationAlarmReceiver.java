package com.mocklibraryapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mocklibraryapplication.Core.Entry;
import com.mocklibraryapplication.Core.Library;

import java.util.ArrayList;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 5413;

    public NotificationAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Change <= 20 to some method
        // an Intent broadcast.

        //TODO: Accessing MainActivity sometimes gives a NullPointerExeption, Solve by importing the booklist from the SharedPref

        String text = "";
        ArrayList<String> notificationList = new ArrayList<String>();



        //Import Library from SharedPrefs
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson =  Converters.registerLocalDate(new GsonBuilder()).create();   //Convert To JSON to store in SharedPref, using a library to parse jodatime (joda-time-serializer)
        String json = mPrefs.getString("Library", "");
        Library tempLibrary = gson.fromJson(json, Library.class);
        if (tempLibrary == null) {
            Log.d(this.getClass().getSimpleName(), "No Library imported");
            return;
        }

        for ( Entry entry : tempLibrary.getLibrary()) {
            if (entry.getDaysLeft() <= 20) {
                notificationList.add (entry.getBook().getTitle() + " is due in " + entry.getDaysLeft() + " days");
            }
        }

        Log.e(this.getClass().getSimpleName(), "notificationList: "+ notificationList.toString());

        if (!notificationList.isEmpty()) {

            //TODO: Add Intent for the Notification To go to an Activity
            //Set an Inbox Style for notifications
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            // Moves Messages into the expanded layout
            for (int i=0; i < notificationList.size(); i++)
                inboxStyle.addLine(notificationList.get(i));
            inboxStyle.setSummaryText("Check your book list");
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("You Have Unreturned Books: ");

            Notification bookNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("You Have Unreturned Books")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_book_white_18dp)
                    .setStyle(inboxStyle)
                    .build();

            //Notify
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, bookNotification);
        }

    }
}
