package objects;

import java.io.Serializable;
import java.util.List;

/**
 * A serializable class to represent the university instructor(s) that use the
 * software.
 */
public class Instructor implements Serializable {
    private static int _id = 0;
    private String id;
    private Account account;
    private String name;
    private List<Section> teaching;

    /**
     * Construct an {@code Administrator} with the provided name and account.
     * 
     * @param name    The name (not null).
     * @param account The account. Can be null.
     * @throws NullPointerException If {@code name} is null.
     */
    public Instructor(String name, Account account) {
        if (name == null) {
            throw new NullPointerException("'name' must not be null.");
        }

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
