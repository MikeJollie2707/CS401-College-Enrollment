package objects;

import java.io.Serializable;

public class BodyLogin implements Serializable {
    private String uniName;
    private String loginID;
    private String password;

    public BodyLogin(String uniName, String loginID, String password) {
        this.uniName = uniName;
        this.loginID = loginID;
        this.password = password;
    }

    public String getUniName() {
        return uniName;
    }

    public String getLoginID() {
        return loginID;
    }

    public String getPassword() {
        return password;
    }

}
