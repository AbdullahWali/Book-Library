package com.mocklibraryapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.mocklibraryapplication.Core.Entry;

import java.util.ArrayList;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 5413;

    public NotificationAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Change <= 20 to some method
        // an Intent broadcast.
        String text = "";
        boolean notify = false;
        ArrayList<String> notificationList = new ArrayList<String>();

        for ( int i = 0 ; i < MainActivity.bookList.getAdapter().getCount(); i++) {
            Entry curEntry = (Entry) (MainActivity.bookList.getItemAtPosition(i));
            if (curEntry.getDaysLeft() <= 20) {
                notify = true;
                notificationList.add (curEntry.getBook().getTitle() + " is due in " + curEntry.getDaysLeft() + " days");
            }
        }

        if (notify) {

            //TODO: Add Intent for the Notification To go to an Activity
            //Set an Inbox Style for notifications
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            // Moves Messages into the expanded layout
            for (int i=0; i < notificationList.size(); i++)
                inboxStyle.addLine(notificationList.get(i));
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
