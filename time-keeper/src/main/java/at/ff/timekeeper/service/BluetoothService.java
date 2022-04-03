package at.ff.timekeeper.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.LifecycleService;

import javax.inject.Inject;

import at.ff.timekeeper.di.AppInjector;
import at.ff.timekeeper.di.HasAppComponent;
import timber.log.Timber;

public class BluetoothService extends LifecycleService {

    public final static String ACTION_STOP = "BluetoothService.STOP";

    private BluetoothService service;

    ServiceNotification notification;

    @Inject
    ServiceModel model;

    public static void start(Context context) {
        Timber.i("BluetoothService.start");

        Intent intent = new Intent(context, BluetoothService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    public static void stop(Context context) {
        Timber.i("BluetoothService.stop");

        Intent intent = new Intent(context, BluetoothService.class);
        intent.setAction(ACTION_STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Timber.i("MusicService.onStartCommand");
        if (intent.getAction() == null || !intent.getAction().equals(ACTION_STOP)) {
            Notification notification = this.notification.buildNotification();
            startForeground(ServiceNotification.NOTIFICATION_ID, notification);
            Timber.i("MusicService startForeground");
        }

        ((HasAppComponent<? extends AppInjector>) getApplicationContext()).getAppComponent().inject(this);
        Timber.i("MusicService Dependency Injection");

        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            unbind();
            notification.remove();
            stopForeground(true);
            Timber.i("MusicService stopForeground");

            return START_NOT_STICKY;
        }

        Timber.v("MusicService model.init");

        bind();

        return START_NOT_STICKY;
    }

    private void bind() {

    }

    private void unbind() {

    }


}
