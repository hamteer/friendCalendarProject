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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.frcal.friendcalender.Activities.DateActivity;
import com.frcal.friendcalender.R;

import java.util.TimeZone;

public class NotificationPublisher extends BroadcastReceiver {

    // create notification channel to be able to receive notifications
    public void createNotificationChannel(Context context) {
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

    // create unique NotificationID to be able to identify and stop each notification independently
    public int getUniqueNotificationId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_name), MODE_PRIVATE);
        int previousId = sharedPreferences.getInt(context.getString(R.string.id_preference_name),
                0);
        int newId;
        if (previousId == 0 || previousId == 536870911) {
            newId = 1;
        } else {
            newId = previousId + 1;
        }
        sharedPreferences.edit().putInt(context.getString(R.string.id_preference_name),
                newId).apply();
        return newId;
    }

    // After a notification is created, it is sent to the AlarmManager to be shown at the correct time
    public void scheduleNotification(Context context, String eventId, String title, int id,
                                     long time, int minutes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_name), MODE_PRIVATE);
        boolean notificationsActive = sharedPreferences.getBoolean(
                context.getString(R.string.notifications_preference_name), false);
        if (notificationsActive && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            TimeZone timeZone = TimeZone.getDefault();
            int offset = timeZone.getOffset(time);
            long deviceTime = time - offset;

            Intent intent = new Intent(context, NotificationPublisher.class);
            intent.putExtra(context.getString(R.string.notifications_notification_key),
                    buildNotification(context, eventId, title, id, deviceTime, minutes));
            intent.putExtra(context.getString(R.string.notifications_id_key), id);
            PendingIntent sender = PendingIntent.getBroadcast(context, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, deviceTime - (long) (minutes) * 60 * 1000, sender);
        }
    }

    // The notification is created here
    // It can be deleted via its ActionButton
    // Upon pressing the message, the page of the event is opened
    // Depending if this is the notification 15 or 5 minutes ahead of the event,
    // another ActionButton is added to set up another notification
    private Notification buildNotification(Context context, String eventId, String title, int id,
                                           long time, int minutes) {
        Intent resultIntent = new Intent(context, DateActivity.class);
        resultIntent.setAction(context.getString(R.string.newly_opened_action));
        resultIntent.putExtra(context.getString(R.string.extra_event_key), eventId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1610612733 - id,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent closeIntent = new Intent(context, NotificationPublisher.class);
        closeIntent.putExtra(context.getString(R.string.notifications_id_key), id);
        closeIntent.putExtra(context.getString(R.string.notifications_close_key), true);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(context, 2147483647 - id,
                closeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder;

        if (minutes == 15) {
            Intent snoozeIntent = new Intent(context, NotificationPublisher.class);
            snoozeIntent.putExtra(context.getString(R.string.notifications_event_id_key), eventId);
            snoozeIntent.putExtra(context.getString(R.string.notifications_title_key), title);
            snoozeIntent.putExtra(context.getString(R.string.notifications_id_key), id);
            snoozeIntent.putExtra(context.getString(R.string.notifications_time_key), time);
            snoozeIntent.putExtra(context.getString(R.string.notifications_minutes_key), 5);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 173741823 - id, snoozeIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            builder = new NotificationCompat.Builder(context,
                    context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_calendar_month_black_24)
                    .setContentTitle(title)
                    .setContentText(context.getString(R.string.notification_description))
                    .setWhen(time).setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(
                            resultPendingIntent).setAutoCancel(true).addAction(
                            R.drawable.baseline_notifications_active_48,
                            context.getString(R.string.notification_action_5),
                            snoozePendingIntent).addAction(
                            R.drawable.baseline_notifications_off_48,
                            context.getString(R.string.notification_action_close),
                            closePendingIntent);
        } else {
            builder = new NotificationCompat.Builder(context,
                    context.getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.baseline_calendar_month_black_24)
                    .setContentTitle(title)
                    .setContentText(context.getString(R.string.notification_description))
                    .setWhen(time).setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(
                            resultPendingIntent).setAutoCancel(true).addAction(
                            R.drawable.baseline_notifications_off_48,
                            context.getString(R.string.notification_action_close),
                            closePendingIntent);
        }
        return builder.build();
    }

    // If an event is deleted, its planned notification should also be deleted
    // This method is also used to change a notification if the associated event is modified
    public void cancelNotification(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        pendingIntent.cancel();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    // First, check if an ActionButton has been pressed
    // If yes, the according function is called
    // If not, the function was called to just show the notification, so this is done
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(context.getString(R.string.notifications_id_key)) && intent.hasExtra(
                context.getString(R.string.notifications_event_id_key)) && intent.hasExtra(
                context.getString(R.string.notifications_title_key)) && intent.hasExtra(
                context.getString(R.string.notifications_id_key)) && intent.hasExtra(
                context.getString(R.string.notifications_time_key)) && intent.hasExtra(
                context.getString(R.string.notifications_minutes_key))) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.cancel(
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0));
            scheduleNotification(context,
                    intent.getStringExtra(context.getString(R.string.notifications_event_id_key)),
                    intent.getStringExtra(context.getString(R.string.notifications_title_key)),
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0),
                    intent.getLongExtra(context.getString(R.string.notifications_time_key), 0),
                    intent.getIntExtra(context.getString(R.string.notifications_minutes_key), 5));
        } else if (intent.hasExtra(
                context.getString(R.string.notifications_close_key)) && intent.hasExtra(
                context.getString(R.string.notifications_id_key))) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.cancel(
                    intent.getIntExtra(context.getString(R.string.notifications_id_key), 0));
        } else if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED && intent.hasExtra(
                context.getString(R.string.notifications_notification_key)) && intent.hasExtra(
                context.getString(R.string.notifications_id_key))) {
            Notification notification = intent.getParcelableExtra(
                    context.getString(R.string.notifications_notification_key));
            int id = intent.getIntExtra(context.getString(R.string.notifications_id_key), 0);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                    context);
            notificationManager.notify(id, notification);
        }
    }
}
