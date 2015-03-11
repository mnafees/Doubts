package solutions.doubts.api.models;

import java.io.Serializable;

public class AuthToken implements Serializable {
    private static final String SCHEME = "DBTS";

    private int userId;
    private String username;
    private String token;

    public AuthToken(int userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", SCHEME, username, token);
    }
}
