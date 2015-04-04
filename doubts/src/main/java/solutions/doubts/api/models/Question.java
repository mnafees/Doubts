/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api.models;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Question extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;
    private String slug;
    private String desc;
    private String created;
    private String updated;
    private String title;
    private S3Image image;
    private User author;
    private RealmList<Entity> tags;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return this.updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public S3Image getImage() {
        return this.image;
    }

    public void setImage(S3Image image) {
        this.image = image;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public RealmList<Entity> getTags() {
        return this.tags;
    }

    public void setTags(RealmList<Entity> tags) {
        this.tags = tags;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Question mQuestion;

        private Builder() {
            mQuestion = new Question();
        }

        public Builder title(final String title) {
            mQuestion.title = title;
            return this;
        }

        public Builder image(final S3Image image) {
            mQuestion.image = image;
            return this;
        }

        public Builder description(final String desc) {
            mQuestion.desc = desc;
            return this;
        }

        public Builder tags(final List<String> tags) {
            final RealmList<Entity> list = new RealmList<>();
            for (final String tag: tags) {
                final Entity entity = Entity.newBuilder()
                        .name(tag)
                        .build();
                list.add(entity);
            }
            mQuestion.tags = list;
            return this;
        }

        public Question build() {
            return mQuestion;
        }
    }

}
