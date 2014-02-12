package com.angrykings.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.angrykings.R;
import com.angrykings.activities.MainActivity;
import com.angrykings.activities.OnlineGameActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static int NOTIFICATION_ID = 1;
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

            String partnerIdStr = extras.getString("id");
            Log.i("GCM", "extras="+extras.toString());

            sendNotification(this, "AngryKings", "such turn: " + partnerIdStr, partnerIdStr);
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void sendNotification(Context context, String title, String msg, String partnerId) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, OnlineGameActivity.class);

        intent.putExtra("existingGame", true);
        intent.putExtra("partnerId", partnerId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new Notification.BigTextStyle().bigText(msg));

        notificationBuilder.setContentIntent(pendingIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound);

        long[] pattern = {0,250,0};
        notificationBuilder.setVibrate(pattern);

        nm.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }
}
