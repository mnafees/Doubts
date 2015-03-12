/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import rx.Observable;
import solutions.doubts.api.Query;
import solutions.doubts.api.backend.QuestionApi;
import solutions.doubts.core.util.RestAdapterUtil;

@DatabaseTable(tableName = "questions")
public class Question implements Serializable {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String slug;

    @DatabaseField
    private String created;

    @DatabaseField
    private String updated;

    @DatabaseField
    private String title;

    @DatabaseField(canBeNull = false, foreign = true)
    private User author;

    public int getId() {
        return this.id;
    }

    public String getSlug() {
        return this.slug;
    }

    public String getCreated() {
        return this.created;
    }

    public String getUpdated() {
        return this.updated;
    }

    public String getTitle() {
        return this.title;
    }

    public User getAuthor() {
        return this.author;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private static class Builder {
        Question mQuestion;

        public Builder() {
            mQuestion = new Question();
        }

        public Builder slug(String slug) {
            mQuestion.slug = slug;
            return this;
        }

        public Builder created(String created) {
            mQuestion.created = created;
            return this;
        }

        public Builder updated(String updated) {
            mQuestion.updated = updated;
            return this;
        }

        public Builder title(String title) {
            mQuestion.title = title;
            return this;
        }

        public Builder author(User author) {
            mQuestion.author = author;
            return this;
        }

        public Question build() {
            return mQuestion;
        }
    }

    public static class LocalQuery implements Query<Question> {
        public LocalQuery() {

        }

        @Override
        public Observable<Question> get(int id, String slug) {
            return null;
        }

        @Override
        public void save(Question question) {
        }
    }

    public static class RemoteQuery implements Query<Question> {
        QuestionApi mQuestionApiImpl;

        public RemoteQuery() {
            mQuestionApiImpl = RestAdapterUtil.getRestAdapter().create(QuestionApi.class);
        }

        @Override
        public Observable<Question> get(int id, String slug) {
            return mQuestionApiImpl.get(id, slug);
        }

        @Override
        public void save(Question question) {
            mQuestionApiImpl.save(question);
        }

    }

    private static LocalQuery sLocalQuery;
    private static RemoteQuery sRemoteQuery;

    private Question () {}

    public static LocalQuery getLocal() {
        if(sLocalQuery != null)
            return sLocalQuery;
        else
            return sLocalQuery = new LocalQuery();
    }

    public static RemoteQuery getRemote() {
        if(sRemoteQuery != null)
            return sRemoteQuery;
        else
            return sRemoteQuery = new RemoteQuery();
    }

}
