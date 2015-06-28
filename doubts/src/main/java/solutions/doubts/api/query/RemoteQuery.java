/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.util.List;
import java.util.Map;

import io.realm.RealmObject;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.ServerReponse;
import solutions.doubts.api.ServerResponseCallback;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.User;
import solutions.doubts.internal.ApiConstants;

public class RemoteQuery<T extends RealmObject> {

    private Class<T> mClazz;
    private Context mContext;
    private ServerReponse mServerResponse;

    RemoteQuery(Class<T> clazz, Context context) {
        mClazz = clazz;
        mContext = context;
        mServerResponse = new ServerReponse();
        mServerResponse.setClazz(clazz);
    }

    @SuppressWarnings("unchecked")
    void setServerResponseCallback(ServerResponseCallback callback) {
        mServerResponse.setServerResponseCallback(callback);
    }

    public void create(T object, ProgressCallback progressCallback) {
        mServerResponse.setClazz(JsonObject.class);
        Ion.with(mContext)
                .load("POST", mapClassToUrl(mClazz))
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .progress(progressCallback)
                .setJsonPojoBody(object, new TypeToken<T>(){})
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void getAll(Query.Order order, String sort, int offset) {
        if (order == null) order = Query.Order.desc;
        if (sort == null) sort = "id";
        Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQuery("order", order.name())
                .addQuery("sort", sort)
                .addQuery("page.offset", Integer.toString(offset))
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void filterBy(String parameter, String value) {
        mServerResponse.setClazz(JsonObject.class);
        Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQuery(parameter, value)
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void filterBy(Map<String, List<String>> queryMap) {
        Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz))
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQueries(queryMap)
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void get(int id, String slug) {
        Ion.with(mContext)
                .load("GET", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void update(int id, String slug, T object) {
        mServerResponse.setClazz(JsonObject.class);
        Ion.with(mContext)
                .load("PUT", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .setJsonPojoBody(object, new TypeToken<T>(){})
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    public void delete(int id, String slug) {
        mServerResponse.setClazz(JsonObject.class);
        Ion.with(mContext)
                .load("DELETE", mapClassToUrl(mClazz) + "/" + Integer.toString(id) + "/" + slug)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .asJsonObject()
                .withResponse()
                .setCallback(mServerResponse);
    }

    private String mapClassToUrl(Class clazz) {
        if (clazz.equals(User.class)) {
            return ApiConstants.USER_RESOURCE;
        } else if (clazz.equals(QuestionsResource.class) || clazz.equals(Question.class)) {
            return ApiConstants.QUESTION_RESOURCE;
        } else if (clazz.equals(Answer.class)) {
            return ApiConstants.ANSWER_RESOURCE;
        }
        return null;
    }

}
