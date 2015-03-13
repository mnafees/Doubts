/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "answers")
public class Answer implements Serializable {

    @DatabaseField(canBeNull = false, id = true)
    private int id;

    @DatabaseField
    private String created;

    @DatabaseField
    private String updated;

    @DatabaseField(foreign = true)
    private User author;

    @DatabaseField
    private String title;

    @DatabaseField
    private String slug;

    @DatabaseField
    private S3Image image;

    @DatabaseField
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
