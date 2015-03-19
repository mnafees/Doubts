/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import java.io.Serializable;

public class Answer implements Serializable {

    private int id;
    private String created;
    private String updated;
    private User author;
    private String title;
    private String slug;
    private S3Image image;
    private Entity question;

    public int getId() {
        return this.id;
    }

    public String getCreated() {
        return this.created;
    }

    public String getUpdated() {
        return this.updated;
    }

    public User getAuthor() {
        return this.author;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSlug() {
        return this.slug;
    }

    public S3Image getImage() {
        return this.image;
    }

    public Entity getQuestion() {
        return this.question;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder {

        private Answer mAnswer;

        private Builder() {
            mAnswer = new Answer();
        }

        public Builder created(String created) {
            mAnswer.created = created;
            return this;
        }

        public Builder updated(String updated) {
            mAnswer.updated = updated;
            return this;
        }

        public Builder author(User author) {
            mAnswer.author = author;
            return this;
        }

        public Builder title(String title) {
            mAnswer.title = title;
            return this;
        }

        public Builder slug(String slug) {
            mAnswer.slug = slug;
            return this;
        }

        public Builder question(Entity question) {
            mAnswer.question = question;
            return this;
        }

        public Answer build() {
            return mAnswer;
        }

    }

}
