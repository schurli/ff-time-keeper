package at.ff.timekeeper.service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.lifecycle.LifecycleService;

import javax.inject.Inject;

import at.ff.timekeeper.R;
import at.ff.timekeeper.data.model.TimerState;
import at.ff.timekeeper.di.AppInjector;
import at.ff.timekeeper.di.HasAppComponent;
import timber.log.Timber;

public class BluetoothService extends LifecycleService {

    private static boolean isBound = false;

    public final static String ACTION_STOP = "BluetoothService.STOP";

    private BluetoothService service;

    ServiceNotification notification;

    @Inject
    ServiceModel model;

    private TimerState timerState = TimerState.ATTACK;

    private MediaPlayer mediaPlayerAttack;
    private MediaPlayer mediaPlayerStartBell;
    private MediaPlayer mediaPlayerStopBell;

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
        Timber.i("BluetoothService.onStartCommand");
        if (intent.getAction() == null || !intent.getAction().equals(ACTION_STOP)) {
            Notification notification = this.notification.buildNotification();
            startForeground(ServiceNotification.NOTIFICATION_ID, notification);
            Timber.i("BluetoothService startForeground");
        }

        ((HasAppComponent<? extends AppInjector>) getApplicationContext()).getAppComponent().inject(this);
        Timber.i("BluetoothService Dependency Injection");

        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            unbind();
            notification.remove();
            stopForeground(true);
            Timber.i("BluetoothService stopForeground");

            return START_NOT_STICKY;
        }

        Timber.v("BluetoothService model.init");

        bind();

        return START_NOT_STICKY;
    }

    private void bind() {
        if (isBound) {
            return;
        }
        isBound = true;
        model.timeKeeper().observe(service, ts -> {
            if (ts != null) {
                Timber.v("time keeper %d", ts);
            }
        });
        // store runs
        model.run().observe(service, run -> {
            if (run != null) {
                Timber.i("store new run %s", run.toString());
                model.save(run);
                model.completeRunTransaction();
            }
        });
        // play audio
        model.timerState().observe(service, timerState -> {
            Timber.i("%s -> %s", this.timerState.name(), timerState.name());
            mediaPlayerAttack.stop();
            mediaPlayerAttack.release();
            mediaPlayerAttack = MediaPlayer.create(service, R.raw.angriffsbefehl);
            mediaPlayerStartBell.stop();
            mediaPlayerStartBell.release();
            mediaPlayerStartBell = MediaPlayer.create(service, R.raw.bell);
            mediaPlayerStopBell.stop();
            mediaPlayerStopBell.release();
            mediaPlayerStopBell = MediaPlayer.create(service, R.raw.bell);

            if (TimerState.ATTACK.equals(this.timerState) && TimerState.START.equals(timerState)) {
                mediaPlayerAttack.start();
                Timber.i("mediaPlayerAttack.start()");
            }
            if (!TimerState.STOP.equals(this.timerState) && TimerState.STOP.equals(timerState)) {
                mediaPlayerStartBell.start();
                Timber.i("mediaPlayerStartBell.start()");
            }
            if (TimerState.STOP.equals(this.timerState) && TimerState.ATTACK.equals(timerState)) {
                mediaPlayerStopBell.start();
                Timber.i("mediaPlayerStopBell.start()");
            }
            this.timerState = timerState;
        });

    }

    private void unbind() {

        if (!isBound) {
            return;
        }
        isBound = false;
        model.timeKeeper().removeObservers(service);
        model.run().removeObservers(service);
        model.timerState().removeObservers(service);
        model.unbind();


    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.service = this;
        notification = new ServiceNotification(getApplication());
        mediaPlayerAttack = MediaPlayer.create(service, R.raw.angriffsbefehl);
        mediaPlayerStartBell = MediaPlayer.create(service, R.raw.bell);
        mediaPlayerStopBell = MediaPlayer.create(service, R.raw.bell);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbind();
        notification.remove();
    }
}
