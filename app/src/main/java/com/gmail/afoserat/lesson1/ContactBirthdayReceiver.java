package com.gmail.afoserat.lesson1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import static android.app.Activity.RESULT_OK;

public class ContactBirthdayReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL-BIRTHDAY";
    private static final String CONTACT_ID = "CONTACT_ID";
    private static final String CONTACT_NAME = "CONTACT_NAME";
    private static final String BIRTHDAY_MESSAGE = "BIRTHDAY_MESSAGE";
    private static final String NOTIFICATION_TITLE = "Birthday";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final PendingResult result = goAsync();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int id = bundle.getInt(CONTACT_ID);
                    String name = bundle.getString(CONTACT_NAME);
                    String message = bundle.getString(BIRTHDAY_MESSAGE);

                    Intent contactDetailsIntent = new Intent(context, MainActivity.class);
                    contactDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    contactDetailsIntent.putExtra(CONTACT_ID, id);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, id, contactDetailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(NOTIFICATION_TITLE)
                            .setContentText(message + name)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(id, builder.build());
                }
                result.setResultCode(RESULT_OK);
                result.finish();
            }
        });
        thread.start();
    }
}
