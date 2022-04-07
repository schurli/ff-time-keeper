package at.ff.timekeeper.service;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import at.ff.timekeeper.data.SharedPrefRepository;
import at.ff.timekeeper.service.observable.TimeKeeperLiveData;

public class ServiceModel  {

    private final SharedPrefRepository sharedPrefRepository;
    private final TimeKeeperLiveData timeKeeperLiveData;

    @Inject
    public ServiceModel(SharedPrefRepository sharedPrefRepository, TimeKeeperLiveData timeKeeperLiveData) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.timeKeeperLiveData = timeKeeperLiveData;
    }

    public LiveData<Long> timeKeeperLiveData() {
        return this.timeKeeperLiveData;
    }


}
