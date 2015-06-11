/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.internal;

import java.io.Serializable;

public class AuthToken implements Serializable {
    private static final String SCHEME = "DBTS";

    private int mUserId;
    private String mUsername;
    private String mToken;

    public AuthToken(int userId, String username, String token) {
        mUserId = userId;
        mUsername = username;
        mToken = token;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", SCHEME, mUsername, mToken);
    }
}
