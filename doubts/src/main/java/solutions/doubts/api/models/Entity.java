/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import java.io.Serializable;

import io.realm.RealmObject;

public class Entity extends RealmObject implements Serializable {

    private int id;
    private String name;
    private String slug;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        Entity mTag;

        private Builder() {
            mTag = new Entity();
        }

        public Builder id(int id) {
            mTag.id = id;
            return this;
        }

        public Builder name(String name) {
            mTag.name = name;
            return this;
        }

        public Builder slug(String slug) {
            mTag.slug = slug;
            return this;
        }

        public Entity build() {
            return mTag;
        }

    }

}
