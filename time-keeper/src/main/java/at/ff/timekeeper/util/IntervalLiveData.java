package at.ff.timekeeper.util;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

public class IntervalLiveData extends MutableLiveData<Long> {

    private final Handler handler;

    private final long interval;

    /**
     * Constructor for live data with periodic time update.
     * @param handler OS handler to use.
     * @param intervalMillis interval in which updates will be sent.
     */
    public IntervalLiveData(Handler handler, long intervalMillis) {
        this.handler = handler;
        this.interval = intervalMillis;
    }

    private boolean active = false;

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (active) {
                postValue(System.currentTimeMillis());
                handler.postDelayed(this, interval);
            }
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        active = true;
        updateRunnable.run();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        active = false;
        handler.removeCallbacks(updateRunnable);
    }

}
