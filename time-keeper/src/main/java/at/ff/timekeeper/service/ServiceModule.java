package at.ff.timekeeper.service;

import android.os.Handler;

import javax.inject.Singleton;

import at.ff.timekeeper.ble.observable.BleMessageLiveData;
import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.data.repository.RunRepository;
import at.ff.timekeeper.service.observable.BleButtonLiveData;
import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Singleton
    @Provides
    ServiceModel serviceModel(SharedPrefRepository sharedPrefRepository, RunRepository runRepository, TimeKeeperLiveData timeKeeperLiveData) {
        return new ServiceModel(sharedPrefRepository, runRepository, timeKeeperLiveData);
    }

    @Singleton
    @Provides
    TimeKeeperLiveData timeKeeperLiveData(Handler handler, SharedPrefRepository sharedPrefRepository, BleMessageLiveData bleMessageLiveData) {
        return new TimeKeeperLiveData(handler, sharedPrefRepository, new BleButtonLiveData(sharedPrefRepository.getBleStartButton(), bleMessageLiveData), new BleButtonLiveData(sharedPrefRepository.getBleStopButton(), bleMessageLiveData));
    }

}
