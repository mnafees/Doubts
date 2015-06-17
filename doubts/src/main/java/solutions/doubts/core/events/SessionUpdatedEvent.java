/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import solutions.doubts.api.models.User;

public class SessionUpdatedEvent {

    private User mUser;

    public SessionUpdatedEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

}
