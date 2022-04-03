package at.ff.timekeeper.di;

import at.ff.timekeeper.service.BluetoothService;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public interface AppInjector extends AndroidInjector<DaggerApplication> {

    @Override
    void inject(DaggerApplication instance);

    void inject(BluetoothService instance);

}
