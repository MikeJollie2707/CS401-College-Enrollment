package objects;

import java.io.Serializable;
import java.util.List;

public class Instructor implements Serializable {
    private static int _id = 0;
    private String id;
    private Account account;
    private String name;
    private List<Section> teaching;

    public Instructor(String name, Account account) {
        id = String.format("instructor_%d", _id);
        ++_id;

        this.account = account;
        this.name = name;
    }

    public synchronized String getID() {
        return id;
    }

    public synchronized Account getAccount() {
        return account;
    }

    public synchronized String getName() {
        return name;
    }
}
