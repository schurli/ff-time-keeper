package at.ff.timekeeper.ble;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class BleDevice {


    public BluetoothDevice device;
    public long lastSeen;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleDevice bleDevice = (BleDevice) o;
        return Objects.equals(device.getAddress(), bleDevice.device.getAddress());
    }


}
