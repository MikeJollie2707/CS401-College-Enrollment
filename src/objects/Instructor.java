package objects;

import java.util.List;

public class Instructor {
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

    public String getID() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }
}
