package at.ff.timekeeper.service.observable;

import android.os.Handler;

import androidx.lifecycle.MediatorLiveData;

import at.ff.timekeeper.util.IntervalLiveData;

public class TimeKeeperLiveData extends MediatorLiveData<Long> {

    private Long startTs = null;

    public TimeKeeperLiveData(Handler handler, BleButtonLiveData startButton, BleButtonLiveData stopButton) {

        addSource(startButton, start -> {
            if (start != null && start) {
               executeStart();
            }
        });
        addSource(stopButton, stop -> {
            if (stop != null && stop) {
               executeStop();
            }
        });
        addSource(new IntervalLiveData(handler, 100), ts -> {
            if (startTs != null) {
                postValue(ts - startTs);
            }
        });

    }

    public void executeStart() {
        if (startTs == null) {
            startTs = System.currentTimeMillis();
        }
    }

    public void executeStop() {
        if (startTs != null) {
            postValue(System.currentTimeMillis() - startTs);
            startTs = null;
        }
    }
}
