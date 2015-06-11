package solutions.doubts.api.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;
    private String name;
    private String username;
    private String email;
    private String created;
    private String updated;
    private int questionCount;
    private S3Image image;
    private String bio;
    private RealmList<Entity> bookmarked;
    private RealmList<UserFollow> followers;
    private RealmList<UserFollow> following;
    private RealmList<Entity> tags;
    
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(final String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(final String updated) {
        this.updated = updated;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(final int questionCount) {
        this.questionCount = questionCount;
    }

    public S3Image getImage() {
        return image;
    }

    public void setImage(final S3Image image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(final String bio) {
        this.bio = bio;
    }

    public RealmList<Entity> getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(final RealmList<Entity> bookmarked) {
        this.bookmarked = bookmarked;
    }

    public RealmList<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(final RealmList<UserFollow> followers) {
        this.followers = followers;
    }

    public RealmList<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(final RealmList<UserFollow> following) {
        this.following = following;
    }

    public RealmList<Entity> getTags() {
        return tags;
    }

    public void setTags(final RealmList<Entity> tags) {
        this.tags = tags;
    }

    public static Builder newUser() {
        return new Builder();
    }

    public static class Builder {

        private User mUser;

        private Builder() {
            mUser = new User();
        }

        public Builder id(final int id) {
            mUser.id = id;
            return this;
        }

        public Builder name(final String name) {
            mUser.name = name;
            return this;
        }

        public Builder username(final String username) {
            mUser.username = username;
            return this;
        }

        public Builder email(final String email) {
            mUser.email = email;
            return this;
        }

        public Builder created(final String created) {
            mUser.created = created;
            return this;
        }

        public Builder updated(final String updated) {
            mUser.updated = updated;
            return this;
        }

        public Builder questionCount(final int questionCount) {
            mUser.questionCount = questionCount;
            return this;
        }

        public Builder image(final S3Image image) {
            mUser.image = image;
            return this;
        }

        public Builder bio(final String bio) {
            mUser.bio = bio;
            return this;
        }

        public Builder bookmarked(final RealmList<Entity> bookmarked) {
            mUser.bookmarked = bookmarked;
            return this;
        }

        public Builder followers(final RealmList<UserFollow> followers) {
            mUser.followers = followers;
            return this;
        }

        public Builder following(final RealmList<UserFollow> following) {
            mUser.following = following;
            return this;
        }

        public Builder tags(final RealmList<Entity> tags) {
            mUser.tags = tags;
            return this;
        }

        public User create() {
            return mUser;
        }

    }

}
