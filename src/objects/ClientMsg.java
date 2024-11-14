package objects;

import java.io.Serializable;

/**
 * A serializable class that act as a message coming from the client to the
 * server.
 */
public class ClientMsg implements Serializable {
    private final String method;
    private final String resource;
    // It's possible to maybe remove this field
    // since the server knows who it's talking to anyway.
    private String authorID;
    private Serializable body;

    /**
     * Construct a {@code ClientMsg}.
     * 
     * @param method   Valid values: {@code GET}, {@code CREATE}, {@code DELETE},
     *                 {@code EDIT}.
     * @param resource The resource the client wants to take action on.
     * @param authorID The ID of the client (ie. student's ID).
     * @param body     Additional information needed depending on {@code method} and
     *                 {@code resource}. Can be null.
     * @throws IllegalArgumentException If {@code method} is not one of the stated
     *                                  values.
     * @throws NullPointerException     If any parameters (except {@code body}) are
     *                                  null.
     */
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

    /**
     * A shortcut to check if this message has the same expected {@code method} and
     * {@code resource}.
     * 
     * @param method   The method to check.
     * @param resource The resource to check.
     * @return true if the provided values match with the client's values, false
     *         otherwise.
     */
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
