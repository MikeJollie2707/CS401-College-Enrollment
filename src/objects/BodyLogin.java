package objects;

import java.io.Serializable;

public class BodyLogin implements Serializable {
    private String uniName;
    private String loginID;
    private String password;

    /**
     * BodyLogin Constructor 
     * 
     * @param uniName   The university name.
     * @param loginID   The login ID of the user.
     * @param password  The password for the login ID.
     * @throws IllegalArgumentException if any of the parameters are null
     */
    public BodyLogin(String uniName, String loginID, String password) {
        
    	if (uniName == null) {
            throw new IllegalArgumentException("University Name can't be null.");
        }
        if (loginID == null) {
            throw new IllegalArgumentException("Login ID can't be null.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password can't be null.");
        }
        
        
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
