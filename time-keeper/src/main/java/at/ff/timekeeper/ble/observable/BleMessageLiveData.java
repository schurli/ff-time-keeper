package at.ff.timekeeper.ble.observable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ff.timekeeper.ble.BleDeviceModel;
import at.ff.timekeeper.ble.BleMessage;
import at.ff.timekeeper.ble.BleScanner;
import at.ff.timekeeper.data.model.Registry;
import timber.log.Timber;

public class BleMessageLiveData extends MediatorLiveData<BleMessage> {

    private final Map<String, BleDeviceModel> modelMap = new HashMap<>();

    private Registry registry;

    private final BleScanner bleScanner;

    private final MutableLiveData<Long> disconnectedLiveData = new MutableLiveData<>();

    public BleMessageLiveData(final LiveData<Registry> deviceRegistry, BleScanner bleScanner, BleDeviceModel.Builder builder) {

        this.bleScanner = bleScanner;

        addSource(deviceRegistry, registry -> {
            // Timber.d(registry.toString());

            this.registry = registry;
            removeObsolete();
            updateScanner();
        });

        addSource(bleScanner.foundDevice(), device -> {

            Timber.d("device is %s, registry is %s", device, registry);

            if (device == null || registry == null) {
                return;
            }

            Timber.d("found device %s", device.getAddress());


            if (modelMap.containsKey(device.getAddress())) {
                Timber.w("Device already active %s", device.getAddress());
                return;
            }

            if (!registry.hasMac(device.getAddress())) {
                Timber.w("Device is not in device registry %s", device.getAddress());
            }

            final BleDeviceModel model = builder.build(device, registry.toServiceRegistry(device.getAddress()));

            addSource(model.getBleMessage(), this::postValue);
            addSource(model.disconnected(), mac -> {
                Timber.d("remove device model from map due to disconnect %s", mac);
                disconnectedLiveData.postValue(System.currentTimeMillis());
                removeSource(model.getBleMessage());
                removeSource(model.disconnected());
                modelMap.remove(mac);
                updateScanner();
            });

            modelMap.put(device.getAddress(), model);
            updateScanner();

        });


    }

    public LiveData<Long> getDisconnected() {
        return disconnectedLiveData;
    }

    private void removeObsolete() {
        if (registry == null) {
            return;
        }

        for (String macAddress : modelMap.keySet()) {
            Timber.d("Check %s keep %b", macAddress, registry.getMacs().contains(macAddress));
            if (!registry.getMacs().contains(macAddress)) {
                modelMap.get(macAddress).disconnect();
            }
        }

    }

    private void updateScanner() {
        if (registry == null) {
            return;
        }

        List<String> wanted = new ArrayList<>(registry.getMacs());
        // filter already connected devices
        wanted.removeIf(mac -> modelMap.keySet().contains(mac));
        // set wanted devices in scanner
        bleScanner.setWantedDevices(wanted);
    }


}
