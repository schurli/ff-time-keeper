package at.ff.timekeeper.service;

import androidx.lifecycle.LiveData;

import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.data.entity.RunEntity;
import at.ff.timekeeper.data.model.TimerState;
import at.ff.timekeeper.data.repository.RunRepository;

public class ServiceModel  {

    private final SharedPrefRepository sharedPrefRepository;
    private final RunRepository runRepository;
    private final TimeKeeperLiveData timeKeeperLiveData;

    public ServiceModel(SharedPrefRepository sharedPrefRepository, RunRepository runRepository, TimeKeeperLiveData timeKeeperLiveData) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.runRepository = runRepository;
        this.timeKeeperLiveData = timeKeeperLiveData;
    }

    public LiveData<Long> timeKeeper() {
        return this.timeKeeperLiveData;
    }

    public LiveData<RunEntity> run() {
        return this.timeKeeperLiveData.getRun();
    }

    public void completeRunTransaction() {
        this.timeKeeperLiveData.completeRunTransaction();
    }

    public LiveData<TimerState> timerState() {
        return this.timeKeeperLiveData.getTimerState();
    }

    public void save(RunEntity entity) {
        this.runRepository.insertAll(entity);
    }

    public void unbind() {
        this.timeKeeperLiveData.destroy();;
    }

}
