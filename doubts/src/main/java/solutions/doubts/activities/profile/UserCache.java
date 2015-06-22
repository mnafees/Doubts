/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import solutions.doubts.api.models.User;

public class UserCache {

    private static UserCache sInstance;
    private User mLastSelectedUser;

    public void setLastSelectedUser(User user) {
        mLastSelectedUser = user;
    }

    public User getLastSelectedUser() {
        return mLastSelectedUser;
    }

    public static UserCache getInstance() {
        synchronized (UserCache.class) {
            if (sInstance == null) {
                sInstance = new UserCache();
            }
        }
        return sInstance;
    }

}
