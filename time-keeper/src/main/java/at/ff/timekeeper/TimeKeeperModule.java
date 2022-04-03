package at.ff.timekeeper;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import at.ff.timekeeper.vm.ViewModelModule;
import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class TimeKeeperModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

}
