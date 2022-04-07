package at.ff.timekeeper.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import at.ff.timekeeper.data.Navigation;
import at.ff.timekeeper.data.SharedPrefRepository;


public class MainViewModel extends ViewModel {

    private final SharedPrefRepository sharedPrefRepository;

    @Inject
    public MainViewModel(SharedPrefRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    public LiveData<Navigation> getNavigation() {
        return sharedPrefRepository.getNavigation();
    }

    public void setNavigation(Navigation value) {
        sharedPrefRepository.putNavigation(value);
    }

}
