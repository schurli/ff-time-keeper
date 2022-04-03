package at.ff.timekeeper;

import android.content.Context;

import androidx.multidex.MultiDex;

import at.ff.timekeeper.ble.BleModule;
import at.ff.timekeeper.data.DataModule;
import at.ff.timekeeper.di.AppComponent;
import at.ff.timekeeper.di.DaggerAppComponent;
import at.ff.timekeeper.di.HasAppComponent;
import at.ff.timekeeper.service.ServiceModule;
import at.ff.timekeeper.vm.observable.ObservableModule;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

/**
 * This class is loaded before every other Activity. It is used to configure AppTracing and the Sentry system
 *
 */
public class TimeKeeperApplication extends DaggerApplication implements HasAppComponent<AppComponent> {

    private AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .observableModule(new ObservableModule())
                .timeKeeperModule(new TimeKeeperModule())
                .dataModule(new DataModule())
                .serviceModule(new ServiceModule())
                .bleModule(new BleModule())
                .build();
        appComponent.inject(this);
        return appComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
