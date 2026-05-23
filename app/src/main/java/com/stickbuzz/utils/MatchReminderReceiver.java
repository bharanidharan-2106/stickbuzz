package com.stickbuzz.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.stickbuzz.R;
import com.stickbuzz.ui.user.UserDashboardActivity;

public class MatchReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "match_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        String teamA = intent.getStringExtra("teamA");
        String teamB = intent.getStringExtra("teamB");

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Match Reminders",
                            NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }

        Intent openIntent = new Intent(context, UserDashboardActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Match Starting Soon!")
                        .setContentText(teamA + " vs " + teamB + " is starting now.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}