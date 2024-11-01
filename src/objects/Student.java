package objects;

import java.util.List;

public class Student {
    private static int _id = 0;

    private String id;
    private Account account;
    private String name;
    private List<Section> past_enrollments;
    private List<Section> enrolling;

    public Student(String name, Account account) {
        id = String.format("student_%d", _id);
        ++_id;

        // TODO: Add null check
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
