package at.ff.timekeeper;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import javax.inject.Singleton;

import at.ff.timekeeper.vm.ViewModelModule;
import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class TimeKeeperModule {

    @Provides
    @Singleton
    Context application(Application application) {
        return application;
    }

    @Singleton
    @Provides
    Handler handler() {
        return new Handler(Looper.getMainLooper());
    }

}
