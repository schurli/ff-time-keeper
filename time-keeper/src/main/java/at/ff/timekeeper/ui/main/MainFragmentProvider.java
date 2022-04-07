package at.ff.timekeeper.ui.main;

import at.ff.timekeeper.ui.main.fragment.HomeFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentProvider {

    @ContributesAndroidInjector
    abstract HomeFragment homeFragment();


}
