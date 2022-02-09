package com.auliya.pengaduanpasien.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;
import static androidx.core.app.NotificationCompat.*;

public class NotificationService extends FirebaseMessagingService {
    public static final String CHANNEL_1_ID =  "channel1";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage);
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Builder notificationBuilder =
                new Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_notfi)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setPriority(PRIORITY_HIGH)
                        .setCategory(CATEGORY_MESSAGE)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}