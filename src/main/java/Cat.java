import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Cat extends Thread implements Serializable {
    public static final int SLEEP_TIME = 3000;

    private static final int COUNT = 9;
    private final int id;
    private final Box box;
    private volatile int count;

    private Cat(final int count, final Box box) {
        this.count = count;
        this.box = box;
        id = CatsIdManager.getInstance().getNextId();
        CatHolder.getInstance().addCat(this);
    }

    public Cat(final Box box) {
        count = COUNT;
        this.box = box;
        id = CatsIdManager.getInstance().getNextId();
        CatHolder.getInstance().addCat(this);
    }

    public Cat(final int count, final int id, final Box box) {
        this.count = count;
        this.id = id;
        this.box = box;
        CatHolder.getInstance().replaceCat(this);
    }

    public static Cat clone(final Cat cat) {
        return new Cat(cat.count, cat.box);
    }
    //endregion

    public static Cat cloneWithId(final Cat cat, final Box newBox) {
        return new Cat(cat.count, cat.id, newBox);
    }

    //region Override
    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), box.getPort()));
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(
                    new CatCommandInfo(CatCommandInfo.Command.START, id)
            );

            BoxManager boxManager = BoxManager.getInstance();
            while (true) {
                if (count != 0) {
                    Thread.sleep(SLEEP_TIME);
                    int chooseAct = ((int) (10 * Math.random())) % 3;
                    if (chooseAct == 0) {
                        setCount(count - 1);
                        boxManager.teleport(this);
                        outputStream.writeObject(
                                new CatCommandInfo(CatCommandInfo.Command.FINISH, id)
                        );
                        outputStream.close();
                        socket.close();
                        interrupt();
                    } else if (chooseAct == 1) {
                        boxManager.clone(this);
                    } else if (chooseAct == 2) {
                    }
                } else {
                    socket.close();
                    interrupt();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ignored) {
        }
    }

    //region public
    public void recharge() {
        this.setCount(COUNT);
    }

    public void kill() {
        this.setCount(0);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    //endregion

    //region getters
    public boolean isdead() {
        return (count == 0);
    }

    public Box getBox() {
        return box;
    }

    public int getCatId() {
        return id;
    }
    //endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cat cat = (Cat) o;
        return id == cat.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cat{" +
                "count=" + count +
                ", id=" + id +
                '}';
    }
}
