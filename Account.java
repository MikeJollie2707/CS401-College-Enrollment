public class Account {
    private String email;
    private String password;
    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public Boolean verify(String email, String password) {
        return (this.email == email) && (this.password == password);
    }
}
