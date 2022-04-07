package at.ff.timekeeper.ble;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import timber.log.Timber;

public class BleScanner {

    private BluetoothLeScannerCompat scanner = null;

    // discovered devices are removed from list
    private final List<String> wantedDevices = new ArrayList<>();

    private final ScannerStateLiveData scannerState = new ScannerStateLiveData(false);

    private final MutableLiveData<BluetoothDevice> foundDeviceLiveData = new MutableLiveData<>();

    private Application application;
    private Handler handler;
    private boolean scanActive = false;
    // Scanning settings
    private final ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(false)
            .build();

    public BleScanner(Application application, Handler handler) {
        this.application = application;
        this.scanner = getScanner();
        this.handler = handler;
    }

    private BluetoothLeScannerCompat getScanner() {
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Timber.w("ACCESS_COARSE_LOCATION not granted.");
            return null;
        }
        return BluetoothLeScannerCompat.getScanner();
    }

    public LiveData<BluetoothDevice> foundDevice() {
        return foundDeviceLiveData;
    }

    public void setWantedDevices(List<String> macs) {

        this.wantedDevices.clear();
        this.wantedDevices.addAll(macs);
        if (wantedDevices.size() > 0) {
            startScan();
        }

    }

    private void foundDevice(BluetoothDevice device) {

        Timber.i("BleScanner.foundDevice: %s", device.getAddress());

        for (String wanted : wantedDevices) {
            Timber.d("found device %s == %s %b", wanted, device.getAddress(), wanted.equals(device.getAddress()));
            if (wanted.equals(device.getAddress())) {
                Timber.d("found device %s, update live data", wanted);
                foundDeviceLiveData.postValue(device);
            }
        }

        wantedDevices.removeIf(mac -> device.getAddress().equals(mac));
        if (wantedDevices.size() == 0) {
            stopScan();
        }

    }

    public void startScan() {

        if (scannerState.isScanning()) {
            return;
        }

        if (!scanActive) {
            startScanning.run();
        } else {
            handler.removeCallbacks(stopScanning);
        }

        scannerState.scanningStarted();

    }

    private void scheduleStartScan() {
        handler.postDelayed(startScanning, 5000);
    }

    public void stopScan() {

        if (!scannerState.isScanning()) {
            return;
        }

        if (scanActive) {
            handler.postDelayed(stopScanning, 5000);
        } else {
            handler.removeCallbacks(startScanning);
        }
        scannerState.scanningStopped();

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

            scannerState.scanningStopped();
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
