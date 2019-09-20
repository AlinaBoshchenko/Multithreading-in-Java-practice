import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Box extends Thread implements Serializable {
    private final int port;
    private final int capacity;
    private final ArrayList<Cat> cats;

    public Box(int port, int capacity) {
        this.port = port;
        this.capacity = capacity;
        this.cats = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket socket;

        try {
            serverSocket = new ServerSocket(port);

            for (Cat c : cats) {
                c.start();
            }

            while (true) {
                try {
                    socket = serverSocket.accept();
                    new ConnectionHandler(socket).start();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return port == box.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port);
    }

    public void generateCats(int count) {
        for (int i = 0; i < count; i++) {
            Cat newCat = new Cat(this);
            addCat(newCat);
        }
    }

    public synchronized void clean() {
        System.out.println("CLEAN");
        for (Cat cat : cats) {
            if (cat.isdead()) {
                removeCat(cat);
            }
        }
    }

    public synchronized void killCats() {
        for (Cat cat : cats) {
            cat.setCount(0);
        }
    }

    public ArrayList<Cat> getCats() {
        return cats;
    }

    public int getCatsSize() {
        return cats.size();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getPort() {
        return port;
    }


    public void removeCat(final Cat cat) {
//        System.out.println("Remove cat: " + cat + " from " + this);
        cats.remove(cat);
    }

    public void addCat(final Cat cat) {
//        System.out.println("Add cat: " + cat + " from " + this);
        cats.add(cat);
    }

    @Override
    public String toString() {
        return "Box{" +
                "port=" + port +
                ", capacity=" + capacity +
                '}';
    }

    private class ConnectionHandler extends Thread {

        private Socket socket;

        public ConnectionHandler(final Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            CatHolder catHolder = CatHolder.getInstance();
            try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                while (!socket.isClosed()) {
                    CatCommandInfo catCommandInfo = (CatCommandInfo) inputStream.readObject();
                    System.out.println("CAT CALLBACK TO BOX " + port + ": " + catCommandInfo.getCommand().name() + " FROM " + catCommandInfo.getCatId());
                    switch (catCommandInfo.getCommand()) {
                        case START:
                            if (cats.size() <= capacity) {
                                addCat(catHolder.findCatById(catCommandInfo.getCatId()));
                            } else {
                                socket.close();
                            }
                            break;
                        case FINISH:
                            removeCat(catHolder.findCatById(catCommandInfo.getCatId()));
                            socket.close();
                            return;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
