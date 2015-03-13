/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

import retrofit.client.Response;
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

    @DatabaseField(canBeNull = false, foreign = true)
    private List<Entity> tags;

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

    public List<Entity> getTags() {
        return this.tags;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Question mQuestion;

        public Builder title(String title) {
            mQuestion.title = title;
            return this;
        }

        public Builder tags(List<Entity> tags) {
            mQuestion.tags = tags;
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
        public Observable<Response> save(Question question) {
            return null;
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
        public Observable<Response> save(Question question) {
            return mQuestionApiImpl.save(question);
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
