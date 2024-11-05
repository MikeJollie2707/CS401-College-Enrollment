package objects;

import java.io.Serializable;

public class ServerMsg implements Serializable {
    private final String status;
    private Serializable body;

    public ServerMsg(String status, Serializable body) {
        this.status = status;
        this.body = body;
    }

    public static ServerMsg asOK(Serializable body) {
        return new ServerMsg("OK", body);
    }

    public static ServerMsg asERR(Serializable body) {
        return new ServerMsg("ERR", body);
    }

    public boolean isOk() {
        return status.equals("OK");
    }

    public Serializable getBody() {
        return body;
    }
}
