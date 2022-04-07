package at.ff.timekeeper.vm.observable;

import android.app.Application;

import javax.inject.Singleton;

import at.ff.timekeeper.ble.observable.BleScanLiveData;
import dagger.Module;
import dagger.Provides;

@Module
public class ObservableModule {

    @Singleton
    @Provides
    BleButtonsLiveData bleButtonsLiveData(Application application, BleScanLiveData bleScanLiveData) {
        return new BleButtonsLiveData(application, bleScanLiveData);
    }

}
