package at.ff.timekeeper.vm;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import at.ff.timekeeper.data.Navigation;
import at.ff.timekeeper.data.SharedPrefRepository;

public class HomeViewModel extends BasicViewModel {

    @Inject
    public HomeViewModel(SharedPrefRepository sharedPrefRepository) {
        super(sharedPrefRepository);
    }

    @Override
    public Navigation.Location getCurrentLocation() {
        return Navigation.Location.HOME;
    }

    @Override
    public void onBack() {

    }
}
