package at.ff.timekeeper.ble;

public class BleMessage {
    long timestamp;
    String mac;
    String service;
    String characteristic;
    byte[] payload;

    public long getTimestamp() {
        return timestamp;
    }

    public String getMac() {
        return mac;
    }

    public String getService() {
        return service;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public byte[] getPayload() {
        return payload;
    }
}
