package at.ff.timekeeper.ble.observable;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.ff.timekeeper.ble.BleDevice;
import at.ff.timekeeper.util.IntervalLiveData;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import timber.log.Timber;

/**
 * This live data performs a ble scan for devices and manages a list of discovered devices.
 * Devices disappear after 60 seconds of inactivity.
 */
public class BleScanLiveData extends MediatorLiveData<List<BluetoothDevice>> {

    private static final long MAX_AGE = 60_000;

    private BluetoothLeScannerCompat scanner = null;

    private final List<BleDevice> bleDevices = new ArrayList<>();

    private final IntervalLiveData intervalLiveData;

    private final Application application;
    private final Handler handler;
    private boolean scanActive = false;

    // Scanning settings
    private final ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(false)
            .build();

    public BleScanLiveData(Application application, Handler handler) {
        this.application = application;
        this.scanner = getScanner();
        this.handler = handler;
        intervalLiveData = new IntervalLiveData(handler, 1000);
        addSource(intervalLiveData, update -> {
            long now = System.currentTimeMillis();
            if (bleDevices.removeIf(bleDevice -> bleDevice.lastSeen + MAX_AGE < now)) {
                update();
            }
        });

    }

    private BluetoothLeScannerCompat getScanner() {
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return BluetoothLeScannerCompat.getScanner();
    }

    private void foundDevice(BluetoothDevice device) {

        Timber.i("BleScanLiveData.foundDevice: %s %s", device.getAddress(), device.getName());

        BleDevice obj = new BleDevice();
        obj.device = device;
        obj.lastSeen = System.currentTimeMillis();

        boolean update = !bleDevices.contains(obj);
        bleDevices.remove(obj);
        bleDevices.add(obj);
        if (update) {
            update();
        }

    }

    private void update() {

        List<BluetoothDevice> devices = new ArrayList<>();
        for (BleDevice bleDevice : bleDevices) {
            devices.add(bleDevice.device);
        }
        // Timber.i("Found %d devices.", devices.size());
        postValue(devices);

    }

    @Override
    protected void onActive() {
        super.onActive();
        startScan();
    }

    private void startScan() {

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null || !adapter.isEnabled()) {
            Timber.e("Bluetooth not available.");
            return;
        }

        if (!scanActive) {
            startScanning.run();
        } else {
            handler.removeCallbacks(stopScanning);
        }
    }

    private void scheduleStartScan() {
        handler.postDelayed(startScanning, 5000);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (scanActive) {
            handler.postDelayed(stopScanning, 5000);
        } else {
            handler.removeCallbacks(startScanning);
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {

            // This callback will be called only if the scan report delay is not set or is set to 0.
            foundDevice(result.getDevice());


        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {

            // This callback will be called only if the report delay set above is greater then 0.
            for (ScanResult result : results) {
                foundDevice(result.getDevice());
            }

        }

        @Override
        public void onScanFailed(final int errorCode) {
            Timber.e("onScanFailed(%d)", errorCode);
            scanActive = false;
        }

    };

    private final Runnable stopScanning = () -> {
        if (scanner == null) {
            scanner = getScanner();
        }
        if (scanner == null || !scanActive) {
            return;
        }
        scanActive = false;
        scanner.stopScan(scanCallback);
    };

    private final Runnable startScanning = () -> {
        Timber.d("Runnable: startScanning.run()");
        if (scanner == null) {
            scanner = getScanner();
        }
        if (scanner == null || !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            scheduleStartScan();
            return;
        }
        if (scanActive) {
            return;
        }
        scanActive = true;
        // Android > 8.1.0 does not allow scanning without filters with screen turned off.
        // does NOT work with Samsung devices.
        // see https://stackoverflow.com/a/48079800
        ScanFilter.Builder builder = new ScanFilter.Builder();
        ScanFilter filter = builder.build();
        scanner.startScan(Collections.singletonList(filter), settings, scanCallback);
    };

}
