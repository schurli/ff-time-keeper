package at.ff.timekeeper.ble.observable;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.location.LocationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import at.ff.timekeeper.util.Permissions;


public class BleAvailableLiveData extends MutableLiveData<BleAvailableLiveData.BleAvailable> {

    private final Application application;
    private final LocationManager locationManager;

    private final BleAvailable bleAvailable = new BleAvailable();

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            update();
        }
    };

    public BleAvailableLiveData(Application application) {
        this.application = application;
        this.locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    private void update() {
        String[] missingPermissions = Permissions.INSTANCE.missing(application, Permissions.BLE);
        bleAvailable.hasPermission = missingPermissions.length == 0;
        bleAvailable.isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager) || Build.VERSION.SDK_INT <= Build.VERSION_CODES.P;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleAvailable.hasBluetooth = bluetoothAdapter != null;
        bleAvailable.isBluetoothEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        postValue(bleAvailable);
    }

    @Override
    protected void onActive() {
        super.onActive();
        application.registerReceiver(broadcastReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        application.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        update();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        application.unregisterReceiver(broadcastReceiver);
    }

    public static class BleAvailable {
        public boolean hasPermission;
        public boolean hasBluetooth;
        public boolean isBluetoothEnabled;
        public boolean isLocationEnabled;
    }

}
