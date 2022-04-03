package at.ff.timekeeper.data;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

public class SharedPrefRepository {

    private final static String NAVIGATION = "preference_navigation_v1";



    private final SharedPrefLiveData<Navigation> navigation;

    private final SharedPrefLiveData.Factory factory;

    public SharedPrefRepository(SharedPrefLiveData.Factory factory) {

        this.factory = factory;
        navigation = factory.build(NAVIGATION, Navigation.class);

    }

    public LiveData<Navigation> getNavigation() {
        return navigation;
    }

    public void putNavigation(Navigation obj) {
        navigation.postValue(obj);
    }

    @NotNull
    @Override
    public String toString() {
        return factory.toString();
    }

}
