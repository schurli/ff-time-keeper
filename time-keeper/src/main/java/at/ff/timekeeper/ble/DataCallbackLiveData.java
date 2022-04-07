package at.ff.timekeeper.ble;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;

import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.data.Data;

public class DataCallbackLiveData extends MediatorLiveData<BleMessage> implements DataReceivedCallback, DataSentCallback {

    private final String service;
    private final String characteristic;

    public DataCallbackLiveData(String service, String characteristic) {
        this.service = service;
        this.characteristic = characteristic;
    }

    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        BleMessage message = new BleMessage();
        message.timestamp = System.currentTimeMillis();
        message.mac = device.getAddress();
        message.service = service;
        message.characteristic = characteristic;
        message.payload = data.getValue();
        postValue(message);
    }

    @Override
    public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
        BleMessage message = new BleMessage();
        message.timestamp = System.currentTimeMillis();
        message.mac = device.getAddress();
        message.service = service;
        message.characteristic = characteristic;
        message.payload = data.getValue();
        postValue(message);
    }
}
