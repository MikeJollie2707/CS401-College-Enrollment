package objects;

import java.io.Serializable;

public class BodyLoginSuccess implements Serializable {
    private String role;
    private Serializable client;

    public BodyLoginSuccess(String role, Serializable client) {
        this.role = role;
        this.client = client;
    }

    public String getRole() {
        return role;
    }

    public Serializable getClient() {
        return client;
    }

}
