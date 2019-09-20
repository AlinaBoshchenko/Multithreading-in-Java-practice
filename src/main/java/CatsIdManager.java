public class CatsIdManager {

    private static CatsIdManager sInstance;
    private int lastId = 1;

    private CatsIdManager() {
    }

    public static CatsIdManager getInstance() {
        if (sInstance == null) {
            sInstance = new CatsIdManager();
        }
        return sInstance;
    }

    public int getNextId() {
        return lastId++;
    }

}
