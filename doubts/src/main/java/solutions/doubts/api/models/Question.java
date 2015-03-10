/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import retrofit.RestAdapter;
import rx.Observable;
import solutions.doubts.api.Query;
import solutions.doubts.api.backend.QuestionApi;

@DatabaseTable(tableName = "questions")
public class Question implements Serializable {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String slug;

    @DatabaseField
    private String title;

    @DatabaseField(canBeNull = false, foreign = true)
    private User author;

    private static RestAdapter mRestAdapter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public static void setRestAdapter(RestAdapter restAdapter) {
        mRestAdapter = restAdapter;
    }

    public class Builder {
        Question mQuestion;

        public Builder() {
            mQuestion = new Question();
        }

        public Builder slug(String slug) {
            mQuestion.slug = slug;
            return this;
        }

        public Builder title(String title) {
            mQuestion.title = title;
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
        public void save(String authHeader, Question question) {
        }
    }

    public static class RemoteQuery implements Query<Question> {
        QuestionApi mQuestionApiImpl;

        public RemoteQuery() {
            mQuestionApiImpl = mRestAdapter.create(QuestionApi.class);
        }

        @Override
        public Observable<Question> get(int id, String slug) {
            return mQuestionApiImpl.get(id, slug);
        }

        @Override
        public void save(String authHeader, Question question) {
            mQuestionApiImpl.save(authHeader, question);
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
