package at.ff.timekeeper.service.observable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import at.ff.timekeeper.ble.BleMessage;
import at.ff.timekeeper.ble.observable.BleMessageLiveData;
import at.ff.timekeeper.data.BleButton;
import timber.log.Timber;

public class BleButtonLiveData extends MediatorLiveData<Boolean> {

    private final long POST_THRESHOLD = 1000L;

    private BleButton bleButton;
    private BleMessage bleMessage;
    private long lastPost = 0L;

    public BleButtonLiveData(LiveData<BleButton> bleButtonLiveData, BleMessageLiveData bleMessageLiveData) {

        addSource(bleButtonLiveData, bleButton -> {
            this.bleButton = bleButton;
            update();
        });

        addSource(bleMessageLiveData, message -> {
            this.bleMessage = message;
            update();
        });

    }

    private void update() {
        if (bleButton == null) {
            postValue(false);
            return;
        }
        if (bleMessage != null && bleMessage.getMac().equals(bleButton.mac)) {
            Timber.i("payload(%d)", bleMessage.getPayload()[0]);
            if (bleMessage.getPayload()[0] == 1 && System.currentTimeMillis() - lastPost > POST_THRESHOLD) {
                lastPost = System.currentTimeMillis();
                postValue(true);
            }
            return;
        }
    }

}
