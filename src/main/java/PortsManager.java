
public class PortsManager {

    private static PortsManager sInstance;
    private int lastPort = 30000;

    private PortsManager() {
    }

    public static PortsManager getInstance() {
        if (sInstance == null) {
            sInstance = new PortsManager();
        }
        return sInstance;
    }

    public int getNextPort() {
        return lastPort++;
    }
}
