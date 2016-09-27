package edu.uco.captainplanet.myapplication;

public class UserInfoApplication {
    private static UserInfoApplication instance;

    private String username;
    private String password;

    public static synchronized UserInfoApplication getInstance() {
        if (instance == null) {
            instance = new UserInfoApplication();
        }
        return instance;
    }

    private UserInfoApplication() {
        setUsername("");
        setPassword("");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
