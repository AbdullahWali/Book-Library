package com.mocklibraryapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mocklibraryapplication.Core.Entry;
import com.mocklibraryapplication.Core.Library;

import java.util.ArrayList;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 5413;
    private static int DAYS_LEFT_NOTIFY = 5; //Notify when DAYS_LEFT_NOTIFY days are left before Deadline
    public NotificationAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Set Alarm After Reboot
        Utilities.setAlarmIfRequired(context);

        ArrayList<String> notificationList = new ArrayList<String>();

        //Import Library from SharedPrefs
        Library tempLibrary = Utilities.loadLibraryFromPref(context);
        if (tempLibrary == null) {
            Log.d(this.getClass().getSimpleName(), "No Library imported");
            return;
        }

        // TODO: Add setting for DAYS_LEFT_NOTIFY
        for ( Entry entry : tempLibrary.getLibrary()) {
            if (entry.getDaysLeft() <= DAYS_LEFT_NOTIFY) {
                notificationList.add (entry.getBook().getTitle() + " is due in " + entry.getDaysLeft() + " days");
            }
        }

        Log.e(this.getClass().getSimpleName(), "notificationList: "+ notificationList.toString());

        if (!notificationList.isEmpty()) {

            //Set an Inbox Style for notifications
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            // Moves Messages into the expanded layout
            for (int i=0; i < notificationList.size(); i++)
                inboxStyle.addLine(notificationList.get(i));
            inboxStyle.setSummaryText("Check your book list");
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("You Have Unreturned Books: ");

            Intent returnIntent = new Intent(context.getApplicationContext() , MainActivity.class);
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context.getApplicationContext(),
                            0,
                            returnIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            Notification bookNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("You Have Unreturned Books")
                    .setSmallIcon(R.drawable.ic_book_white_18dp)
                    .setStyle(inboxStyle)
                    .setContentIntent(pendingIntent)
                    .setLights(0xff00ff00,1000,100)
                    .setAutoCancel(true)
                    .build();

            //Add Vibrate, Sound, and Lights
            bookNotification.defaults |= Notification.DEFAULT_VIBRATE;
            bookNotification.defaults |= Notification.DEFAULT_SOUND;



            //Notify
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, bookNotification);
        }

    }
}
