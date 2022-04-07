package at.ff.timekeeper.data;


import java.util.Objects;

public class BleButton {

    public String mac;
    public String label;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BleButton ble = (BleButton) o;
        return Objects.equals(mac, ble.mac) &&
                Objects.equals(label, ble.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac, label);
    }
}
