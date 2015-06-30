package solutions.doubts.api;

import java.util.List;

import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.User;

public class FollowingResource {
    private int count;
    private List<User> following;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }
}
