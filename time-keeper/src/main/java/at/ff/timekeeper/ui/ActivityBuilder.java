package at.ff.timekeeper.ui;

import at.ff.timekeeper.vm.ViewModelModule;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {
            ViewModelModule.class
    })
    abstract MainActivity bindMainActivity();

}