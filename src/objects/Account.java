package objects;

import java.io.Serializable;

/**
 * A lightweight serializable class to store account information.
 */
public class Account implements Serializable {
    private String email;
    private String password;

    /**
     * Construct an {@code Account} with the provided email and password.
     * 
     * @param email    The email (not null).
     * @param password The password (not null).
     * @throws NullPointerException If any parameters are null.
     */
    public Account(String email, String password) {
        if (email == null || password == null) {
            throw new NullPointerException("Arguments for constructor must not be null.");
        }
        this.email = email;
        this.password = password;
    }

    /**
     * Verify whether the provided email and password matches the one stored by this
     * account.
     * 
     * @param email    The email.
     * @param password The password.
     * @return true if the provided email and password matches this account, false
     *         otherwise.
     */
    public boolean verify(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public boolean isSimilar(Account account) {
        return this.email.equals(account.email) && this.password.equals(account.password);
    }
}
