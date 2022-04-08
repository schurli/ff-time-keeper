package at.ff.timekeeper.ble;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.Collections;

import at.ff.timekeeper.data.model.BleButton;
import at.ff.timekeeper.data.model.Command;
import at.ff.timekeeper.data.model.Registry;

public class RegistryLiveData extends MediatorLiveData<Registry> {

    private final Registry registry = new Registry();
    private BleButton start;
    private BleButton stop;

    public RegistryLiveData(LiveData<BleButton> startBleButton, LiveData<BleButton> stopBleButton) {

        addSource(startBleButton, button -> {
            start = button;
            update();
        });

        addSource(stopBleButton, bleButton -> {
            stop = bleButton;
            update();
        });

    }

    private void update() {

        registry.clear();

        if (start != null) {
            registry.put(String.format("%s/%s/%s/%s", "LOCALHOST", start.mac, Service.BLE_BUTTON, Characteristic.BLE_BUTTON), Collections.singletonList(Command.READ));
        }
        if (stop != null) {
            registry.put(String.format("%s/%s/%s/%s", "LOCALHOST", stop.mac, Service.BLE_BUTTON, Characteristic.BLE_BUTTON), Collections.singletonList(Command.READ));
        }

        postValue(registry);

    }
}
