package at.ff.timekeeper.service;

import android.os.Handler;

import javax.inject.Singleton;

import at.ff.timekeeper.ble.observable.BleMessageLiveData;
import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.service.observable.BleButtonLiveData;
import at.ff.timekeeper.service.observable.TimeKeeperLiveData;
import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Singleton
    @Provides
    ServiceModel serviceModel(SharedPrefRepository sharedPrefRepository, TimeKeeperLiveData timeKeeperLiveData) {
        return new ServiceModel(sharedPrefRepository, timeKeeperLiveData);
    }

    @Singleton
    @Provides
    TimeKeeperLiveData timeKeeperLiveData(Handler handler, SharedPrefRepository sharedPrefRepository, BleMessageLiveData bleMessageLiveData) {
        return new TimeKeeperLiveData(handler, new BleButtonLiveData(sharedPrefRepository.getBleStartButton(), bleMessageLiveData), new BleButtonLiveData(sharedPrefRepository.getBleStopButton(), bleMessageLiveData));
    }


}
