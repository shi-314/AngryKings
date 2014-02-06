package com.angrykings.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        Log.i("GCM", "onHandleIntent: type=" + messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
                return;

            String message = extras.getString("msg");
            Log.i("GCM", "receive: " + message);
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
