package at.ff.timekeeper.ui;

import at.ff.timekeeper.ui.main.BleActivity;
import at.ff.timekeeper.ui.main.MainActivity;
import at.ff.timekeeper.ui.main.MainFragmentProvider;
import at.ff.timekeeper.vm.ViewModelModule;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {
            MainFragmentProvider.class,
            ViewModelModule.class,
    })
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = {
            ViewModelModule.class
    })
    abstract BleActivity bindBleActivity();

}