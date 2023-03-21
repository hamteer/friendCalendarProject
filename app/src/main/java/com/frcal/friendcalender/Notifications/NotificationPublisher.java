package com.frcal.friendcalender.Notifications;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.frcal.friendcalender.Activities.DateActivity;
import com.frcal.friendcalender.R;

public class NotificationPublisher extends BroadcastReceiver {
    public int getUniqueNotificationId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_name),MODE_PRIVATE );
        int previousId = sharedPreferences.getInt("id_preference_name", 0);
        int newId;
        if (previousId == 0 || previousId == 2147483647) {
            newId = 1;
        } else {
            newId = previousId + 1;
        }
        sharedPreferences.edit().putInt("id_preference_name", newId).apply();
        return newId;
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    context.getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =
                    context.getSystemService(
                            NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(Context context, String eventId, String title, int id,
                                     long time, int minutes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_name), MODE_PRIVATE);
        boolean notificationsActive = sharedPreferences.getBoolean(
                context.getString(R.string.notifications_preference_name), false);
        if (notificationsActive && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(context, NotificationPublisher.class);
            intent.putExtra(context.getString(R.string.notifications_notification_key),
                    buildNotification(context, eventId, title, id, time, minutes));
            intent.putExtra(context.getString(R.string.notifications_id_key), id);
            PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, time - (long) (minutes) * 60 * 1000, sender);
        }
    }

    private Notification buildNotification(Context context, String eventId, String title, int id,
                                           long time, int minutes) {
        Intent resultIntent = new Intent(context, DateActivity.class);
        resultIntent.setAction("android.intent.action.VIEW_LOCUS");
        resultIntent.putExtra("SELECTED_EVENT", eventId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(context, NotificationPublisher.class);
        closeIntent.putExtra(context.getString(R.string.notifications_id_key), id);
        closeIntent.putExtra("notifications_close_key", true);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(context, 1, closeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder;

        if (minutes == 15) {
            Intent snoozeIntent = new Intent(context, NotificationPublisher.class);
            snoozeIntent.putExtra("notifications_event_id_key", eventId);
            snoozeIntent.putExtra("notifications_title_key", title);
            snoozeIntent.putExtra(context.getString(R.string.notifications_id_key), id);
            snoozeIntent.putExtra("notifications_time_key", time);
            snoozeIntent.putExtra("notifications_minutes_key", 5);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder = new NotificationCompat.Builder(context,
                    context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_calendar_month_black_24)
                    .setContentTitle(title)
                    .setContentText(context.getString(R.string.notification_description_15))
                    .setWhen(time).setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(
                            resultPendingIntent).setAutoCancel(true).addAction(
                            R.drawable.baseline_notifications_active_48,
                            "5 Minuten vorher nochmal erinnern", snoozePendingIntent).addAction(
                            R.drawable.baseline_notifications_off_48, "Schließen",
                            closePendingIntent);
        } else {
            builder = new NotificationCompat.Builder(context,
                    context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_calendar_month_black_24)
                    .setContentTitle(title)
                    .setContentText(context.getString(R.string.notification_description_5))
                    .setWhen(time).setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(
                            resultPendingIntent).setAutoCancel(true).addAction(
                            R.drawable.baseline_notifications_off_48, "Schließen",
                            closePendingIntent);
        }
        return builder.build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("notifications_minutes_key")) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.cancel(
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0));
            scheduleNotification(context, intent.getStringExtra("notifications_event_id_key"),
                    intent.getStringExtra("notifications_title_key"),
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0),
                    intent.getLongExtra("notifications_time_key", 0),
                    intent.getIntExtra("notifications_minutes_key", 5));
        } else if (intent.hasExtra("notifications_close_key")) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.cancel(
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0));
        } else if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Juhu", Toast.LENGTH_SHORT).show();
            Notification notification = intent.getParcelableExtra(
                    context.getString(R.string.notifications_notification_key));
            int id = intent.getIntExtra(context.getString(R.string.notifications_id_key), 0);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.notify(id, notification);
        }
    }
}
