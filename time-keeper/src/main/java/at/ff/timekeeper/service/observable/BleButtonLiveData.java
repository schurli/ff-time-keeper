package at.ff.timekeeper.service.observable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.Arrays;

import at.ff.timekeeper.ble.BleMessage;
import at.ff.timekeeper.ble.observable.BleMessageLiveData;
import at.ff.timekeeper.data.model.BleButton;
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

        addSource(bleMessageLiveData.getDisconnected(), mac -> {
            if (bleButton != null) {
                if (bleButton.mac.equals(mac)) {
                    destroy();
                }
            }
        });

    }

    private void update() {
        if (bleButton == null) {
            postValue(false);
            return;
        }
        if (bleMessage != null && bleMessage.getMac().equals(bleButton.mac)) {
            Timber.i("%s payload(%s)", bleButton.mac, Arrays.toString(bleMessage.getPayload()));

            // hack to avoid event on connection
            if (lastPost == 0L) {
                lastPost = System.currentTimeMillis() - POST_THRESHOLD;
                return;
            }

            if (bleMessage.getPayload()[0] == 0 && System.currentTimeMillis() - lastPost > POST_THRESHOLD) {
                lastPost = System.currentTimeMillis();
                postValue(true);
            }
            return;
        }
    }

    public void destroy() {
        this.lastPost = 0L;
    }

}
