package objects;

import java.io.Serializable;

public class ClientMsg implements Serializable {
    private final String method;
    private final String resource;
    // It's possible to maybe remove this field
    // since the server knows who it's talking to anyway.
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

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }
}
