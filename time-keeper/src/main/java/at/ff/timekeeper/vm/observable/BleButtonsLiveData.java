package at.ff.timekeeper.vm.observable;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import at.ff.timekeeper.ble.observable.BleScanLiveData;
import at.ff.timekeeper.data.BleButton;
import timber.log.Timber;

public class BleButtonsLiveData extends MediatorLiveData<List<BleButton>> {

    public BleButtonsLiveData(Application application, BleScanLiveData bleScanLiveData) {

        addSource(bleScanLiveData, devices -> {

            List<BleButton> bleButtons = new ArrayList<>();
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    BleButton bleButton = new BleButton();
                    if (ActivityCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        bleButton.label = "N/A";
                    } else {
                        bleButton.label = device.getName() == null ? "N/A" : device.getName().toUpperCase();
                    }
                    bleButton.mac = device.getAddress();
                    bleButtons.add(bleButton);
                }
            }
            Timber.d("ble buttons %d", bleButtons.size());
            postValue(bleButtons);

        });

    }

}
