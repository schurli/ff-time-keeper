package at.ff.timekeeper.data;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

public class SharedPrefRepository {

    // settings
    private final static String DEVICE_REGISTRY = "preference_device_registry_v1";
    private final static String BLE_START_BUTTON = "preference_ble_start_button_v1";
    private final static String BLE_STOP_BUTTON = "preference_ble_stop_button_v1";
    private final static String START_TIMESTAMP = "preference_start_timestamp_v1";
    private final static String TIME_KEEPER_ACTIVE = "preference_time_keeper_active_v1";

    // automatically updated
    private final static String NAVIGATION = "preference_navigation_v1";

    private final SharedPrefLiveData<Registry> deviceRegistry;
    private final SharedPrefLiveData<BleButton> bleStartButton;
    private final SharedPrefLiveData<BleButton> bleStopButton;
    private final SharedPrefLiveData<Long> startTimestamp;
    private final SharedPrefLiveData<Boolean> timeKeeperActive;

    private final SharedPrefLiveData<Navigation> navigation;

    private final SharedPrefLiveData.Factory factory;

    public SharedPrefRepository(SharedPrefLiveData.Factory factory) {

        this.factory = factory;

        // settings
        deviceRegistry = factory.build(DEVICE_REGISTRY, Registry.class);
        bleStartButton = factory.build(BLE_START_BUTTON, BleButton.class);
        bleStopButton = factory.build(BLE_STOP_BUTTON, BleButton.class);
        startTimestamp = factory.build(START_TIMESTAMP, Long.class);
        timeKeeperActive = factory.build(TIME_KEEPER_ACTIVE, Boolean.class);

        // automatically updated
        navigation = factory.build(NAVIGATION, Navigation.class);

    }

    public LiveData<Registry> getDeviceRegistry() {
        return deviceRegistry;
    }

    public void putDeviceRegistry(Registry value) {
        deviceRegistry.postValue(value);
    }

    public LiveData<BleButton> getBleStartButton() {
        return bleStartButton;
    }

    public void putBleStartButton(BleButton value) {
        this.bleStartButton.postValue(value);
    }

    public LiveData<BleButton> getBleStopButton() {
        return bleStopButton;
    }

    public void putBleStopButton(BleButton value) {
        this.bleStopButton.postValue(value);
    }

    public LiveData<Long> getStartTimestamp() {
        return startTimestamp;
    }

    public void putStartTimestamp(Long value) {
        startTimestamp.postValue(value);
    }

    public LiveData<Boolean> getTimeKeeperActive() {
        return timeKeeperActive;
    }

    public void putTimeKeeperActive(Boolean value) {
        timeKeeperActive.postValue(value);
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
