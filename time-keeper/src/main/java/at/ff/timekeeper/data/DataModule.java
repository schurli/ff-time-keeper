package at.ff.timekeeper.data;

import android.app.Application;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Singleton
    @Provides
    SharedPrefRepository sharedPrefRepository(Application application) {
        return new SharedPrefRepository(new SharedPrefLiveData.Factory(PreferenceManager.getDefaultSharedPreferences(application)));
    }

}
