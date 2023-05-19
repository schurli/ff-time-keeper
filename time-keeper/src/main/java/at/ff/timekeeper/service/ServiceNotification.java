package at.ff.timekeeper.service;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import at.ff.timekeeper.R;
import at.ff.timekeeper.ui.main.MainActivity;

public class ServiceNotification {

    public static final int NOTIFICATION_ID = 49500;

    private static final String CHANNEL_ID = "time_keeper_channel";
    private final PendingIntent activityIntent;

    private final NotificationCompat.Builder builder;
    private final NotificationManager notificationManager;
    private final Application application;

    public ServiceNotification(final Application application) {

        this.application = application;
        notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, application.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Intent implicitActivityIntent = new Intent(application, MainActivity.class);
        implicitActivityIntent.addCategory(Intent.CATEGORY_DEFAULT);
        implicitActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent implicitStopIntent = new Intent(application, BluetoothService.class);
        implicitStopIntent.setAction(BluetoothService.ACTION_STOP);

        activityIntent = PendingIntent.getActivity(application, 0, implicitActivityIntent, FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(application, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_timer)
                .setContentTitle(application.getString(R.string.app_name))
                .setContentText(application.getString(R.string.notification_text_none))
                .setContentIntent(activityIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());

    }

    public Notification buildNotification() {

        return builder.build();

    }

    public void remove() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
