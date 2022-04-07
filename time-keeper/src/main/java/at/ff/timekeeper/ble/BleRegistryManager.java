package at.ff.timekeeper.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import at.ff.timekeeper.data.Command;
import at.ff.timekeeper.data.Registry;
import no.nordicsemi.android.ble.BleManager;
import timber.log.Timber;

public class BleRegistryManager extends BleManager {

    private final Registry registry;
    private final Map<String, List<BluetoothGattCharacteristic>> map;
    private final MediatorLiveData<BleMessage> liveData = new MediatorLiveData<>();
    private final List<LiveData<BleMessage>> sourceList = new ArrayList<>();

    private BleManagerGattCallback gattCallback;


    public BleRegistryManager(Context context, Registry registry) {
        super(context);
        this.registry = registry;
        Timber.d(registry.toString());
        this.map = new HashMap<>();
    }

    public MediatorLiveData<BleMessage> getBleMessage() {
        return liveData;
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        if (gattCallback == null) {
            Timber.d("gattCallback is null.");
            gattCallback = gattCallback = new BleManagerGattCallback() {

                @Override
                protected void initialize() {

                    for (String serviceString : map.keySet()) {
                        Registry charRegistry = registry.toCharacteristicRegistry(serviceString);
                        for (BluetoothGattCharacteristic characteristic : Objects.requireNonNull(map.get(serviceString))) {
                            DataCallbackLiveData callback = new DataCallbackLiveData(serviceString, characteristic.getUuid().toString());
                            sourceList.add(callback);
                            runOnCallbackThread(() -> {
                                liveData.addSource(callback, liveData::postValue);
                            });
                            Registry cmdRegistry = charRegistry.toCommandRegistry(characteristic.getUuid().toString());
                            for (Command cmd : cmdRegistry.getCommands()) {
                                if (cmd.type.equals(Command.Type.READ)) {
                                    setNotificationCallback(characteristic).with(callback);
                                    readCharacteristic(characteristic).with(callback).enqueue();
                                    enableNotifications(characteristic).enqueue();
                                }
                                if (cmd.type.equals(Command.Type.WRITE)) {
                                    writeCharacteristic(characteristic, cmd.payload).with(callback).enqueue();
                                }
                            }
                        }
                    }

                }

                @Override
                protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {

                    for (String serviceString : registry.getServices()) {

                        final BluetoothGattService service = gatt.getService(UUID.fromString(serviceString));
                        if (service != null) {
                            final List<BluetoothGattCharacteristic> list = new ArrayList<>();
                            for (String charString : registry.toCharacteristicRegistry(serviceString).getCharacteristics()) {
                                BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(charString));
                                if (characteristic != null) {
                                    list.add(characteristic);
                                }
                            }
                            map.put(serviceString, list);
                        }

                    }

                    return true; // require nothing
                }

                @Override
                protected void onDeviceDisconnected() {
                    map.clear();
                    runOnCallbackThread(() -> {
                        for (LiveData<BleMessage> source : sourceList) {
                            liveData.removeSource(source);
                        }
                    });

                    sourceList.clear();
                }
            };
        }
        return gattCallback;
    }

}
