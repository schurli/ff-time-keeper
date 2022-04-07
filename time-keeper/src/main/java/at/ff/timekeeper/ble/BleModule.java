package at.ff.timekeeper.ble;

import android.app.Application;
import android.os.Handler;

import javax.inject.Singleton;

import at.ff.timekeeper.ble.observable.BleAvailableLiveData;
import at.ff.timekeeper.ble.observable.BleMessageLiveData;
import at.ff.timekeeper.ble.observable.BleScanLiveData;
import at.ff.timekeeper.data.SharedPrefRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class BleModule {

    @Provides
    @Singleton
    public BleScanLiveData bleScan(Application application, Handler handler) {
        return new BleScanLiveData(application, handler);
    }

    @Singleton
    @Provides
    BleMessageLiveData bleMessageLiveData(Application application, Handler handler, SharedPrefRepository sharedPrefRepository) {
        BleDeviceModel.Builder builder = new BleDeviceModel.Builder(application);

        return new BleMessageLiveData(
                sharedPrefRepository.getDeviceRegistry(),
                new BleScanner(application, handler),
                builder
        );
    }

    @Singleton
    @Provides
    BleAvailableLiveData bleAvailableLiveData(Application application) {
        return new BleAvailableLiveData(application);
    }

}
