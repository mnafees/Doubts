/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Answer extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;
    private String created;
    private String updated;
    private User author;
    private String title;
    private String desc;
    private String slug;
    private S3Image image;
    private Entity question;
    private RealmList<Entity> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public S3Image getImage() {
        return image;
    }

    public void setImage(S3Image image) {
        this.image = image;
    }

    public Entity getQuestion() {
        return question;
    }

    public void setQuestion(Entity question) {
        this.question = question;
    }

    public RealmList<Entity> getTags() {
        return tags;
    }

    public void setTags(RealmList<Entity> tags) {
        this.tags = tags;
    }

    public static Builder newAnswer() {
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

        public Builder desc(String desc) {
            mAnswer.desc = desc;
            return this;
        }

        public Builder tags(RealmList<Entity> tags) {
            mAnswer.tags = tags;
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
