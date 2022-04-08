package at.ff.timekeeper.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.data.entity.RunEntity;
import at.ff.timekeeper.data.model.BleButton;
import at.ff.timekeeper.data.model.Navigation;
import at.ff.timekeeper.data.model.TimerState;
import at.ff.timekeeper.data.repository.RunRepository;
import at.ff.timekeeper.service.TimeKeeperLiveData;
import at.ff.timekeeper.vm.observable.BleButtonsLiveData;

public class HomeViewModel extends ViewModel {

    private final SharedPrefRepository sharedPrefRepository;
    private final RunRepository runRepository;
    private final BleButtonsLiveData bleButtonsLiveData;
    private final TimeKeeperLiveData timeKeeperLiveData;

    @Inject
    public HomeViewModel(SharedPrefRepository sharedPrefRepository, RunRepository runRepository, BleButtonsLiveData bleButtonsLiveData, TimeKeeperLiveData timeKeeperLiveData) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.runRepository = runRepository;
        this.bleButtonsLiveData = bleButtonsLiveData;
        this.timeKeeperLiveData = timeKeeperLiveData;
    }

    public LiveData<Navigation> getNavigation() {
        return sharedPrefRepository.getNavigation();
    }

    public void setNavigation(Navigation value) {
        sharedPrefRepository.putNavigation(value);
    }

    public LiveData<List<BleButton>> getBleButtons() {
        return bleButtonsLiveData;
    }

    public LiveData<BleButton> getBleStartButton() {
        return sharedPrefRepository.getBleStartButton();
    }

    public void setBleStartButton(BleButton value) {
        sharedPrefRepository.putBleStartButton(value);
    }

    public LiveData<BleButton> getBleStopButton() {
        return sharedPrefRepository.getBleStopButton();
    }

    public void setBleStopButton(BleButton value) {
        sharedPrefRepository.putBleStopButton(value);
    }

    public void executeStart() {
        timeKeeperLiveData.executeStart();
    }

    public void executeStop() {
        timeKeeperLiveData.executeStop();
    }

    public void executeAttack() {
        timeKeeperLiveData.executeAttack();
    }

    public LiveData<Long> timeKeeper() {
        return this.timeKeeperLiveData;
    }

    public LiveData<List<RunEntity>> topRuns(RunEntity.Mode mode) {
        return this.runRepository.topRuns(mode);
    }

    public LiveData<List<RunEntity>> latestRuns(RunEntity.Mode mode) {
        return this.runRepository.latestRuns(mode);
    }

    public LiveData<TimerState> timerState() {
        return this.timeKeeperLiveData.getTimerState();
    }

    public LiveData<RunEntity.Mode> mode() {
        return this.sharedPrefRepository.getMode();
    }

    public void setMode(RunEntity.Mode value) {
        sharedPrefRepository.putMode(value);
    }
}
