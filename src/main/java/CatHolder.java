import java.util.ArrayList;
import java.util.List;

public class CatHolder {
    private static CatHolder instance;
    private final List<Cat> cats = new ArrayList<>();

    public static CatHolder getInstance() {
        if (instance == null) {
            instance = new CatHolder();
        }
        return instance;
    }

    public void addCat(Cat cat) {
        cats.add(cat);
    }

    public Cat findCatById(final int id) {
        for (Cat c : cats) {
            if (c.getCatId() == id) {
                return c;
            }
        }
        return null;
    }

    public void replaceCat(Cat cat) {
        cats.remove(findCatById(cat.getCatId()));
        cats.add(cat);
    }
}
