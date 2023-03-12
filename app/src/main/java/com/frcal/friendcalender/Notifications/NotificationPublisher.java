package com.frcal.friendcalender.Notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.frcal.friendcalender.Activities.DateActivity;
import com.frcal.friendcalender.R;

public class NotificationPublisher extends BroadcastReceiver {

    Context activityContext;
    int notificationId;

    public NotificationPublisher(Context context, int id) {
        activityContext = context;
        notificationId = id;
    }

    private void buildNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                context);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Intent resultIntent = new Intent(context, DateActivity.class);
            resultIntent.setAction("android.intent.action.VIEW");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                    context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_calendar_month_black_24)
                    .setContentTitle(activityContext.getString(R.string.notification_title))
                    .setContentText(activityContext.getString(R.string.notification_text))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setWhen(
                            298492).setContentIntent(
                            resultPendingIntent).setAutoCancel(true);

            notificationManager.notify(notificationId, builder.build());

        }

    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = activityContext.getString(R.string.channel_name);
            String description = activityContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    activityContext.getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    activityContext.getSystemService(
                            NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        buildNotification(context);
    }
}
