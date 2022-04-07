package at.ff.timekeeper.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import at.ff.timekeeper.ble.Characteristic;
import at.ff.timekeeper.ble.Service;
import at.ff.timekeeper.data.BleButton;
import at.ff.timekeeper.data.Command;
import at.ff.timekeeper.data.Navigation;
import at.ff.timekeeper.data.Registry;
import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.service.observable.TimeKeeperLiveData;
import at.ff.timekeeper.vm.observable.BleButtonsLiveData;

public class HomeViewModel extends ViewModel {

    private final SharedPrefRepository sharedPrefRepository;
    private final BleButtonsLiveData bleButtonsLiveData;
    private final TimeKeeperLiveData timeKeeperLiveData;

    @Inject
    public HomeViewModel(SharedPrefRepository sharedPrefRepository, BleButtonsLiveData bleButtonsLiveData, TimeKeeperLiveData timeKeeperLiveData) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.bleButtonsLiveData = bleButtonsLiveData;
        this.timeKeeperLiveData = timeKeeperLiveData;
    }

    public LiveData<Navigation> getNavigation() {
        return sharedPrefRepository.getNavigation();
    }

    public void setNavigation(Navigation value) {
        sharedPrefRepository.putNavigation(value);
    }

    public LiveData<List<BleButton>> getBleButtons() {
        return bleButtonsLiveData;
    }

    public LiveData<BleButton> getBleStartButton() {
        return sharedPrefRepository.getBleStartButton();
    }

    public void setBleStartButton(BleButton value) {
        sharedPrefRepository.putBleStartButton(value);
        Registry registry = new Registry();
        registry.put(String.format("%s/%s/%s/%s", "LOCALHOST", value.mac, Service.BLE_BUTTON, Characteristic.BLE_BUTTON), Collections.singletonList(Command.READ));
        sharedPrefRepository.putDeviceRegistry(registry);
    }

    public LiveData<BleButton> getBleStopButton() {
        return sharedPrefRepository.getBleStopButton();
    }

    public void setBleStopButton(BleButton value) {
        sharedPrefRepository.putBleStopButton(value);
        Registry registry = new Registry();
        registry.put(String.format("%s/%s/%s/%s", "LOCALHOST", value.mac, Service.BLE_BUTTON, Characteristic.BLE_BUTTON), Collections.singletonList(Command.READ));
        sharedPrefRepository.putDeviceRegistry(registry);
    }

    public void executeStart() {
        timeKeeperLiveData.executeStart();
    }

    public void executeStop() {
        timeKeeperLiveData.executeStop();
    }


}
