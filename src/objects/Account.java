package objects;

import java.io.Serializable;

public class Account implements Serializable {
    private String email;
    private String password;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean verify(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}
