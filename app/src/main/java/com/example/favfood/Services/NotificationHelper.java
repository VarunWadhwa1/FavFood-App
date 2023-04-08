package com.example.favfood.Services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.favfood.MainActivity;
import com.example.favfood.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class NotificationHelper extends FirebaseMessagingService{

    private String CHANNEL_ID;
    public NotificationHelper() {
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        
    }
    private void createNotificationChannel() {
        CHANNEL_ID = getString(R.string.default_notification_channel_id);
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences preferences = getSharedPreferences("Features",MODE_PRIVATE);
        boolean isNotificationSelected= preferences.getBoolean("Notification", false);
        if(isNotificationSelected)
        {
            createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.favlogo2)
                    .setContentTitle(remoteMessage.getData().get("title")).setContentText("Your best deal are waiting...")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Your best deal are waiting..."))
                    .setPriority(NotificationCompat.PRIORITY_MAX).setAutoCancel(true);

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
    }
    public void getFirebaseMessagingToken ( ) {
        FirebaseMessaging.getInstance ().getToken ().addOnCompleteListener ( task -> {
                if (!task.isSuccessful ()) {
                    return;
                }
                if (null != task.getResult ()) {
                    String firebaseMessagingToken = Objects.requireNonNull ( task.getResult () );
                }
        });
    }

}