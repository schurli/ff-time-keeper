package at.ff.timekeeper.service;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.data.entity.RunEntity;
import at.ff.timekeeper.data.model.TimerState;
import at.ff.timekeeper.service.observable.BleButtonLiveData;
import at.ff.timekeeper.util.IntervalLiveData;
import timber.log.Timber;

/**
 * main live data broadcasts current time. service must subscribe to it in order to keep updates coming.
 */
public class TimeKeeperLiveData extends MediatorLiveData<Long> {

    // is time keeper running?
    private final MutableLiveData<TimerState> timerStateLiveData = new MutableLiveData<>(TimerState.ATTACK);

    // is updated at completion of a run.
    private final MutableLiveData<RunEntity> runLiveData = new MutableLiveData<>();

    private Long startTs = null;

    private RunEntity.Mode mode = RunEntity.Mode.BRONZE;

    public TimeKeeperLiveData(Handler handler, SharedPrefRepository sharedPrefRepository, BleButtonLiveData startButton, BleButtonLiveData stopButton) {

        addSource(startButton, start -> {
            if (start != null && start) {
                Timber.i("START BUZZER");
                // todo limit start buzzer to start action?
               executeStart();
            }
        });
        addSource(stopButton, stop -> {
            if (stop != null && stop) {
               executeStop();
            }
        });
        addSource(new IntervalLiveData(handler, 50), ts -> {
            if (startTs != null) {
                postValue(ts - startTs);
            }
        });
        addSource(sharedPrefRepository.getMode(), mode -> {
            if (mode != null) {
                this.mode = mode;
            }
        });

    }

    public void executeAttackOrStart() {
        TimerState timerState = timerStateLiveData.getValue() == null ? TimerState.ATTACK : timerStateLiveData.getValue();
        switch (timerState) {
            case ATTACK:
                executeAttack();
                break;
            case START:
                executeStart();
                break;
        }
    }

    public void executeStart() {
        if (startTs == null) {
            startTs = System.currentTimeMillis();
            timerStateLiveData.postValue(TimerState.STOP);
        }
    }

    public void executeStop() {
        if (startTs != null) {
            long endTs = System.currentTimeMillis();
            long duration = endTs - startTs;
            postValue(duration);
            timerStateLiveData.postValue(TimerState.ATTACK);
            runLiveData.postValue(new RunEntity(mode.toString(), startTs, endTs, duration));
            startTs = null;
        }
    }

    public void executeAttack() {
        postValue(null);
        timerStateLiveData.postValue(TimerState.START);
    }

    public LiveData<TimerState> getTimerState() {
        return timerStateLiveData;
    }

    public LiveData<RunEntity> getRun() {
        return runLiveData;
    }
}
