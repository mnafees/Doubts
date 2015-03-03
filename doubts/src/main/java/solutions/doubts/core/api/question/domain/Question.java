/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.api.question.domain;

import java.util.List;

import solutions.doubts.core.api.base.domain.BaseDomain;
import solutions.doubts.core.api.user.domain.User;

public class Question extends BaseDomain {

    private User author;
    private String slug;
    private List<Tag> tags;
    private String title;

    public User getAuthor() {
        return author;
    }

    public String getSlug() {
        return slug;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    private class Tag {

        private int id;
        private String name;
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
    }

}
