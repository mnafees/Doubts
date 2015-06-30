package solutions.doubts.activities.newprofile;

import solutions.doubts.api.models.User;

public interface UserObserver {
    void onUserChanged(User user);
}
