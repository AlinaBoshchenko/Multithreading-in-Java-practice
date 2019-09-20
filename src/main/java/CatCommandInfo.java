import java.io.Serializable;

public class CatCommandInfo implements Serializable {
    private final Command command;
    private final int catId;
    public CatCommandInfo(Command command, int catId) {
        this.command = command;
        this.catId = catId;
    }

    public Command getCommand() {
        return command;
    }

    public int getCatId() {
        return catId;
    }

    public enum Command implements Serializable {
        START,
        FINISH
    }
}
