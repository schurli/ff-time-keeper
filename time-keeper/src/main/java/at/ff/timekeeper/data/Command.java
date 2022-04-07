package at.ff.timekeeper.data;

public class Command {

    public static final Command READ = new Command(Type.READ);

    public Type type;
    public byte[] payload;

    public Command(Type type) {
        this.type = type;
        this.payload = new byte[0];
    }

    public Command(Type type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    public enum Type {
        WRITE,
        READ, // = notify
    }
}
