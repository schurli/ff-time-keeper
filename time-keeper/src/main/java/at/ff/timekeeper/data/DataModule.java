package at.ff.timekeeper.data;

import android.app.Application;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import at.ff.timekeeper.data.dao.RunDao;
import at.ff.timekeeper.data.repository.RunRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Singleton
    @Provides
    SharedPrefRepository sharedPrefRepository(Application application) {
        return new SharedPrefRepository(new SharedPrefLiveData.Factory(PreferenceManager.getDefaultSharedPreferences(application)));
    }

    @Singleton
    @Provides
    AppDatabase providesAppDatabase(Application application) {
        return AppDatabase.getDatabase(application);
    }

    @Singleton
    @Provides
    RunDao providesRunDao(AppDatabase database) {
        return database.runDao();
    }

    @Singleton
    @Provides
    RunRepository providesRunRepository(RunDao dao) {
        return new RunRepository(dao);
    }

}
