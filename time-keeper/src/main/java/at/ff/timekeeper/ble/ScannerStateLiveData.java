package at.ff.timekeeper.ble;

import androidx.lifecycle.LiveData;

public class ScannerStateLiveData extends LiveData<ScannerStateLiveData> {

    private boolean isScanning;

    public ScannerStateLiveData(boolean isScanning) {
        this.isScanning = isScanning;
    }

    void scanningStarted() {
        isScanning = true;
        postValue(this);
    }

    void scanningStopped() {
        isScanning = false;
        postValue(this);
    }

    public boolean isScanning() {
        return isScanning;
    }

}
