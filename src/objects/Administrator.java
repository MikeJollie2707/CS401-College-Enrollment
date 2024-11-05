package objects;

public class Administrator {
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
