package at.ff.timekeeper.di;

public interface HasAppComponent<T extends AppInjector> {

    T getAppComponent();

}
