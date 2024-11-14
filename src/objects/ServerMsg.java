package objects;

import java.io.Serializable;

/**
 * A serializable class that act as a message coming from the server to the
 * client, typically a response to a {@code ClientMsg}.
 * <p>
 * There are two states: {@code OK} and {@code ERR}. {@code OK} is when
 * everything is ok, {@code ERR} is when something went wrong.
 */
public class ServerMsg implements Serializable {
    private final String status;
    private Serializable body;

    /**
     * Construct a {@code ServerMsg}.
     * <p>
     * Consider using {@code ServerMsg.asOK()} or {@code ServerMsg.asERR()} instead.
     * 
     * @param status Valid values: {@code OK}, {@code ERR}.
     * @param body   Additional information depending on the context. Can be null.
     */
    private ServerMsg(String status, Serializable body) {
        this.status = status;
        this.body = body;
    }

    /**
     * Construct an {@code OK} response.
     * 
     * @param body Additional information depending on the context. Can be null.
     * @return A {@code ServerMsg}.
     */
    public static ServerMsg asOK(Serializable body) {
        return new ServerMsg("OK", body);
    }

    /**
     * Construct an {@code ERR} response.
     * 
     * @param body Additional information depending on the context. Should not be
     *             null.
     * @return A {@code ServerMsg}.
     */
    public static ServerMsg asERR(Serializable body) {
        return new ServerMsg("ERR", body);
    }

    /**
     * Whether this is an {@code OK} message or not.
     * 
     * @return true if this is an {@code OK} message, false otherwise.
     */
    public boolean isOk() {
        return status.equals("OK");
    }

    public Serializable getBody() {
        return body;
    }
}
