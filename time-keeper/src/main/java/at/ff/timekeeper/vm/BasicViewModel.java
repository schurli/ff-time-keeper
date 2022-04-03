package at.ff.timekeeper.vm;

import android.media.session.PlaybackState;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import at.ff.timekeeper.data.Navigation;
import at.ff.timekeeper.data.SharedPrefRepository;
import timber.log.Timber;


public abstract class BasicViewModel extends ViewModel {

    final SharedPrefRepository sharedPrefRepository;
    final LiveData<Navigation.Location> locationLiveData;


    protected BasicViewModel(SharedPrefRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        locationLiveData = Transformations.map(sharedPrefRepository.getNavigation(), navigation -> navigation == null ? Navigation.Location.HOME : navigation.location);
    }

    public LiveData<Navigation.Location> getLocation() {
        return locationLiveData;
    }

    protected void putLocation(Navigation.Location location) {
        Timber.i("BasicViewModel.putLocation(%s)", location.name());
        Navigation existing = sharedPrefRepository.getNavigation().getValue();
        if (existing == null) {
            existing = new Navigation();
        }
        existing.location = location;
        sharedPrefRepository.putNavigation(existing);
    }

    public boolean bluetoothRequired() {
        return false;
    }

    public abstract Navigation.Location getCurrentLocation();

    public void onClick(String id) {}

    public abstract void onBack();

    public void onSetting() {}

}
