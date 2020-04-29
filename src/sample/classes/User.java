package sample.classes;

public class User {

    public int accountID;
    public String userName;
    public String password;

    public User(){}

    public User(int accountID, String userName, String password) {
        this.accountID = accountID;
        this.userName = userName;
        this.password = password;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
