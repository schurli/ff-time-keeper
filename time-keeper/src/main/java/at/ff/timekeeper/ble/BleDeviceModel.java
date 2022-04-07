package at.ff.timekeeper.ble;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import at.ff.timekeeper.data.Registry;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import timber.log.Timber;

public class BleDeviceModel extends AndroidViewModel implements BleManagerCallbacks {

    private final MutableLiveData<String> disconnected = new MutableLiveData<>();

    private final BleRegistryManager manager;


    private BleDeviceModel(final Application application, BluetoothDevice bluetoothDevice, Registry registry) {
        super(application);

        manager = new BleRegistryManager(application, registry);
        manager.setGattCallbacks(this);
        manager.connect(bluetoothDevice)
                .retry(3, 100)
                .useAutoConnect(false)
                .enqueue();

    }

    public void disconnect() {
        if (manager.isConnected()) {
            manager.disconnect().enqueue();
        }
    }

    public LiveData<BleMessage> getBleMessage() {
        return manager.getBleMessage();
    }

    public LiveData<String> disconnected() {
        return disconnected;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (manager.isConnected()) {
            manager.disconnect().enqueue();
        }
    }

    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
        Timber.i("Connecting %s", device.getAddress());
    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {
        Timber.i("Discovering services %s", device.getAddress());
    }

    @Override
    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
        disconnected.postValue(device.getAddress());
        Timber.i("Disconnecting %s", device.getAddress());
    }

    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
        disconnected.postValue(device.getAddress());
        Timber.i("Disconnected %s", device.getAddress());
    }

    @Override
    public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
        disconnected.postValue(device.getAddress());
        Timber.i("Link loss %s", device.getAddress());

    }

    @Override
    public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
        Timber.i("Services discovered %s", device.getAddress());
    }

    @Override
    public void onDeviceReady(@NonNull BluetoothDevice device) {
        Timber.i("Device ready %s", device.getAddress());
    }

    @Override
    public void onBondingRequired(@NonNull BluetoothDevice device) {
        Timber.i("Bonding required %s", device.getAddress());
    }

    @Override
    public void onBonded(@NonNull BluetoothDevice device) {
        Timber.i("Bonded %s", device.getAddress());
    }

    @Override
    public void onBondingFailed(@NonNull BluetoothDevice device) {
        Timber.i("Bonding failed %s", device.getAddress());
    }

    @Override
    public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
        Timber.i("%s %s", message, device.getAddress());
    }

    @Override
    public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
        Timber.i("Device not supported %s", device.getAddress());
    }

    public static class Builder {

        private final Application application;

        public Builder(Application application) {
            this.application = application;
        }

        public BleDeviceModel build(BluetoothDevice bluetoothDevice, Registry registry) {
            return new BleDeviceModel(application, bluetoothDevice, registry);
        }

    }

}
