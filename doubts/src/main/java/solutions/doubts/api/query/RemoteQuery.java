/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;
import java.util.Map;

import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.User;
import solutions.doubts.internal.ApiConstants;

public class RemoteQuery<T> {

    private Class<T> mClazz;
    private Context mContext;

    public enum Order {
        desc,
        asc
    }

    public RemoteQuery(Class<T> clazz) {
        mClazz = clazz;
        if (!allowedType(clazz)) {
            throw new IllegalStateException("Class " + clazz.getName() + " is not allowed");
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public Future<Response<JsonObject>> create(T object) {
        return Ion.with(mContext)
                .load("POST", mapClassToUrl(mClazz))
                .setJsonPojoBody(object, new TypeToken<T>(){})
                .asJsonObject()
                .withResponse();
    }

    public Future<Response<List<T>>> getAll(Order order, String sort) {
        return Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .addQuery("order", order.name())
                .addQuery("sort", sort)
                .as(new TypeToken<List<T>>(){})
                .withResponse();
    }

    public Future<Response<List<T>>> filterBy(String parameter, String value) {
        return Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .addQuery(parameter, value)
                .as(new TypeToken<List<T>>() {
                })
                .withResponse();
    }

    public Future<Response<List<T>>> filterBy(Map<String, List<String>> queryMap) {
        return Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .addQueries(queryMap)
                .as(new TypeToken<List<T>>(){})
                .withResponse();
    }

    public Future<Response<T>> get(int id, String slug) {
        return Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .as(new TypeToken<T>(){})
                .withResponse();
    }

    public Future<Response<JsonObject>> update(int id, String slug, T object) {
        return Ion.with(mContext)
                .load("PUT", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .setJsonPojoBody(object, new TypeToken<T>(){})
                .asJsonObject()
                .withResponse();
    }

    public Future<Response<JsonObject>> delete(int id, String slug) {
        return Ion.with(mContext)
                .load("DELETE", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .asJsonObject()
                .withResponse();
    }

    private boolean allowedType(Class<T> clazz) {
        if (clazz.equals(User.class)) {
            return true;
        } else if (clazz.equals(Question.class)) {
            return true;
        } else if (clazz.equals(Answer.class)) {
            return true;
        }
        return false;
    }

    private String mapClassToUrl(Class<T> clazz) {
        if (clazz.equals(User.class)) {
            return ApiConstants.USER_RESOURCE;
        } else if (clazz.equals(Question.class)) {
            return ApiConstants.QUESTION_RESOURCE;
        } else if (clazz.equals(Answer.class)) {
            return ApiConstants.ANSWER_RESOURCE;
        }
        return null;
    }

}
