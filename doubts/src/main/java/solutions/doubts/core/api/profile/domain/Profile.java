/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.api.profile.domain;

public class Profile {

    public static class Builder {

        private String bio;
        private String created;
        private String updated;
        private String email;
        private String name;
        private String username;

        public Builder bio(final String bio) {
            this.bio = bio;
            return this;
        }

        public Builder created(final String date) {
            this.created = date;
            return this;
        }

        public Builder updated(final String date) {
            this.updated = date;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

    }

    private int id;
    private String bio;
    private String created;
    private String updated;
    private String email;
    private String name;
    private String username;
    private String uri;

    private Profile(final String bio,
                    final String created,
                    final String updated,
                    final String email,
                    final String name,
                    final String username) {
        this.bio = bio;
        this.created = created;
        this.updated = updated;
        this.email = email;
        this.name = name;
        this.username = username;
    }

    public String getUri() {
        return this.uri;
    }

    public int getId() {
        return this.id;
    }

    public String getBio() {
        return this.bio;
    }

    public String getCreated() {
        return this.created;
    }

    public String getUpdated() {
        return this.updated;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

}
