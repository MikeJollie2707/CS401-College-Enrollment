package objects;

import java.io.Serializable;

/**
 * A serializable class to represent the university admin(s) that use the
 * software.
 */
public class Administrator implements Serializable {
    private static int _id = 0;

    private String id;
    private Account account;
    private String name;

    /**
     * Construct an {@code Administrator} with the provided name and account.
     * 
     * @param name    The name (not null).
     * @param account The account (not null).
     * @throws NullPointerException If any parameters are null.
     */
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
