import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class BoxManager extends Thread {
    public static final int SLEEP_TIME = 5000;
    private static BoxManager sInstance;
    private final int numOfBoxes;
    private final ArrayList<Box> boxes;

    public BoxManager(int numOfBoxes) {
        this.numOfBoxes = numOfBoxes;
        this.boxes = new ArrayList<>(numOfBoxes);
    }

    public static BoxManager createInstance(final int numOfBoxes) {
        sInstance = new BoxManager(numOfBoxes);
        return sInstance;
    }

    public static BoxManager getInstance() {
        return sInstance;
    }

    //region Override
    @Override
    public void run() {
        for (int i = 0; i < numOfBoxes; i++) {
            final int port = PortsManager.getInstance().getNextPort();
            int boxCapacity = (int) (10 * Math.random());
            boxCapacity = boxCapacity == 0 ? boxCapacity + 1 : boxCapacity;
            int numOfCats = (int) (10 * Math.random()) % boxCapacity;
            Box box = new Box(port, boxCapacity);
            box.generateCats(numOfCats);
            boxes.add(box);
            box.start();
        }

        while (true) {
            try {
                Thread.sleep(SLEEP_TIME);
                Box pickedBox = getRandomBox();
                int action = new Random().nextInt() % 2;
                if (action == 0) {
                    pickedBox.killCats();
                    System.out.println(pickedBox + "->killCats");
                } else {
                    System.out.println(pickedBox + "->Don'tkillCats");
                    pickedBox.clean();
                    rechargeCat(pickedBox);
                    addCats(pickedBox);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    //region private
    private Box getRandomBox() {
        int randomNum = (int) (100 * Math.random() % numOfBoxes);
        return boxes.get(randomNum);
    }

    private void rechargeCat(Box pickedBox) {
        int quant = (int) (100 * Math.random() % pickedBox.getCatsSize());
        ArrayList<Cat> cats = pickedBox.getCats();
        for (int i = 0; i < quant; i++) {
            int catNum = (int) (100 * Math.random() % pickedBox.getCatsSize());
            cats.get(catNum).recharge();
        }
    }

    private void addCats(Box pickedBox) {
        if (pickedBox.getCatsSize() < pickedBox.getCapacity()) {
            int addQuant = (int) (100 * Math.random() % (pickedBox.getCapacity() - pickedBox.getCatsSize()));
            for (int i = 0; i < addQuant; i++) {
                pickedBox.addCat(new Cat(pickedBox));
            }
        }
    }

    public void teleport(Cat cat) {
        Box currentBox = cat.getBox();

        Box newBox = getRandomBox();
        while (currentBox.getId() == newBox.getId()) {
            newBox = getRandomBox();
        }
        System.out.println("TELEPORT " + cat.getCatId() + " FROM " + currentBox.getPort() + " TO " + newBox.getPort());

        Cat catCopy = Cat.cloneWithId(cat, newBox);
        catCopy.start();
    }

    public void clone(Cat cat) {
        Cat newCat = Cat.clone(cat);
        newCat.start();
    }
    //endregion
}
