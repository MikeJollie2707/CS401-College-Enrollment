package objects;

import java.io.Serializable;

public class ClientMsg implements Serializable {
    private final String method;
    private final String resource;
    private String authorID;
    private Serializable body;

    public ClientMsg(String method, String resource, String authorID, Serializable body) {
        this.method = method;
        this.resource = resource;
        this.authorID = authorID;
        this.body = body;
    }

    public String getAuthorID() {
        return authorID;
    }

    public Serializable getBody() {
        return body;
    }

    public boolean isEndpoint(String method, String resource) {
        return this.method.equals(method) && this.resource.equals(resource);
    }
}
