package objects;

import java.io.Serializable;

public class Administrator implements Serializable {
    private static int _id = 0;

    private String id;
    private Account account;
    private String name;

    public Administrator(String name, Account account) {
        id = String.format("admin_%d", _id);
        ++_id;

        this.name = name;
        this.account = account;
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
