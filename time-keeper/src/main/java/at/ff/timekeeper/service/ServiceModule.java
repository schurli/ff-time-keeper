package at.ff.timekeeper.service;

import javax.inject.Singleton;

import at.ff.timekeeper.data.SharedPrefRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Singleton
    @Provides
    ServiceModel serviceModel(SharedPrefRepository sharedPrefRepository) {
        return new ServiceModel(sharedPrefRepository);
    }

}
