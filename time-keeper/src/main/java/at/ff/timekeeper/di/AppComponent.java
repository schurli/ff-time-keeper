package at.ff.timekeeper.di;

import android.app.Application;

import javax.inject.Singleton;

import at.ff.timekeeper.TimeKeeperModule;
import at.ff.timekeeper.ble.BleModule;
import at.ff.timekeeper.data.DataModule;
import at.ff.timekeeper.service.BluetoothService;
import at.ff.timekeeper.service.ServiceModule;
import at.ff.timekeeper.ui.ActivityBuilder;
import at.ff.timekeeper.vm.observable.ObservableModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;


@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ObservableModule.class,
        TimeKeeperModule.class,
        ServiceModule.class,
        DataModule.class,
        BleModule.class,
        ActivityBuilder.class,
})
public interface AppComponent extends AppInjector {

    // each target entity needs its own inject method, because dependencies are calculated at compile-time.
    void inject(BluetoothService application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder observableModule(ObservableModule observableModule);

        @BindsInstance
        Builder timeKeeperModule(TimeKeeperModule timeKeeperModule);

        @BindsInstance
        Builder serviceModule(ServiceModule serviceModule);

        @BindsInstance
        Builder dataModule(DataModule dataModule);

        @BindsInstance
        Builder bleModule(BleModule bleModule);

        AppComponent build();
    }

}