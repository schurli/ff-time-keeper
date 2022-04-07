package at.ff.timekeeper.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import at.ff.timekeeper.ble.observable.BleAvailableLiveData;


public class BleViewModel extends ViewModel {

    private final BleAvailableLiveData bleAvailableLiveData;

    @Inject
    public BleViewModel(BleAvailableLiveData bleAvailableLiveData) {
        this.bleAvailableLiveData = bleAvailableLiveData;
    }

    public LiveData<BleAvailableLiveData.BleAvailable> getBleAvailable() {
        return bleAvailableLiveData;
    }


}
