package com.angrykings.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.angrykings.R;
import com.angrykings.activities.EndGameActivity;
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
            String servermsg = extras.getString("msg");

            Log.i("GCM", "extras="+extras.toString());

            if(extras.getString("msg").equals("turn")){
                sendNotification(this, "AngryKings", getString(R.string.notificationTextTurn), partnerIdStr, servermsg);
            }else if(servermsg.equals("you_win")){
                sendNotification(this, "AngryKings", getString(R.string.notificationTextWin), partnerIdStr, servermsg);
            }else if(servermsg.equals("new_game")){
                sendNotification(this, "AngryKings", getString(R.string.notificationTextNewGame), partnerIdStr, servermsg);
            }else{
                sendNotification(this, "AngryKings", "such turn: " + extras.getString("msg"), partnerIdStr, servermsg);
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void sendNotification(Context context, String title, String msg, String partnerId, String servermsg) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = null;

        if(!servermsg.equals("you_win")){
            intent = new Intent(context, OnlineGameActivity.class);
            intent.putExtra("existingGame", true);
        }else{
            intent = new Intent(context, EndGameActivity.class);
            intent.putExtra("hasWon", true);
        }

        intent.putExtra("partnerId", partnerId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new Notification.BigTextStyle().bigText(msg));

        notificationBuilder.setContentIntent(pendingIntent);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if(settings.getBoolean("notificationsSound", false)) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(alarmSound);
        }

        if(settings.getBoolean("notificationsVibration", false)) {
            long[] pattern = {0,250,0};
            notificationBuilder.setVibrate(pattern);
        }

        nm.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }
}
