/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

// FIXME: adjust accordingly with the DB table
@DatabaseTable(tableName = "tags")
public class Entity implements Serializable {

    @DatabaseField
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String slug;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSlug() {
        return this.slug;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder {

        Entity mTag;

        public Builder() {
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
