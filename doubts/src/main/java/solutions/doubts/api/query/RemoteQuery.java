/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import java.util.List;
import java.util.Map;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.internal.ApiConstants;

public class RemoteQuery<T> {

    private static final String TAG = "RemoteQuery";

    private Class<T> mClazz;
    private Context mContext;
    private Query.Order mOrder;
    private String mSort;
    private int mOffset;

    RemoteQuery(Class<T> clazz, Context context) {
        mClazz = clazz;
        mContext = context;
        mOrder = Query.Order.desc;
        mSort = "id";
    }

    private void sortAndOrderWithOffset(Query.Order order, String sort, int offset) {
        mOrder = order;
        mSort = sort;
        mOffset = offset;
    }

    private void create(String url, T object, ProgressCallback progressCallback,
                        FutureCallback<Response<JsonObject>> futureCallback) {
        Ion.with(mContext)
                .load("POST", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .progress(progressCallback)
                .setJsonPojoBody(object)
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    private void getAll(String url, FutureCallback<Response<T>> futureCallback) {
        Ion.with(mContext)
                .load("GET", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQuery("order", mOrder.name())
                .addQuery("sort", mSort)
                .addQuery("page.offset", Integer.toString(mOffset))
                .as(mClazz)
                .withResponse()
                .setCallback(futureCallback);
    }

    private void filterBy(String url, String parameter, String value,
                          FutureCallback<Response<T>> futureCallback) {
        Ion.with(mContext)
                .load("GET", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQuery(parameter, value)
                .addQuery("order", mOrder.name())
                .addQuery("sort", mSort)
                .addQuery("page.offset", Integer.toString(mOffset))
                .as(mClazz)
                .withResponse()
                .setCallback(futureCallback);
    }

    private void filterBy(String url, Map<String, List<String>> queryMap,
                       FutureCallback<Response<T>> futureCallback) {
        Ion.with(mContext)
                .load("GET", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .addQueries(queryMap)
                .addQuery("order", mOrder.name())
                .addQuery("sort", mSort)
                .addQuery("page.offset", Integer.toString(mOffset))
                .as(mClazz)
                .withResponse()
                .setCallback(futureCallback);
    }

    private void get(String url, FutureCallback<Response<T>> futureCallback) {
        Ion.with(mContext)
                .load("GET", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .as(mClazz)
                .withResponse()
                .setCallback(futureCallback);
    }

    private void update(String url, T object, FutureCallback<Response<JsonObject>> futureCallback) {
        Ion.with(mContext)
                .load("PUT", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .setJsonPojoBody(object)
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    private void delete(String url, FutureCallback<Response<JsonObject>> futureCallback) {
        Ion.with(mContext)
                .load("DELETE", url)
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getSession()
                        .getAuthToken().toString())
                .asJsonObject()
                .withResponse()
                .setCallback(futureCallback);
    }

    public static class Builder<T> {

        private RemoteQuery<T> mRemoteQuery;
        private String mUrl;

        public Builder(Class<T> clazz, Context context) {
            mRemoteQuery = new RemoteQuery<>(clazz, context);
            mUrl = ApiConstants.API_ENDPOINT + "/api/v1";
        }

        public Builder<T> resource(Object... path) {
            for (Object o : path) {
                if (o.getClass().equals(String.class)) {
                    mUrl += "/" + (String)o;
                } else if (o.getClass().equals(Integer.class)) {
                    mUrl += "/" + Integer.toString((int)o);
                }
            }
            return this;
        }

        public Builder<T> sortAndOrderWithOffset(Query.Order order, String sort, int offset) {
            mRemoteQuery.sortAndOrderWithOffset(order, sort, offset);
            return this;
        }

        public void create(T object, ProgressCallback progressCallback,
                           FutureCallback<Response<JsonObject>> futureCallback) {
            mRemoteQuery.create(mUrl, object, progressCallback, futureCallback);
        }

        public void getAll(FutureCallback<Response<T>> futureCallback) {
            mRemoteQuery.getAll(mUrl, futureCallback);
        }

        public void filterBy(String parameter, String value,
                          FutureCallback<Response<T>> futureCallback) {
            mRemoteQuery.filterBy(mUrl, parameter, value, futureCallback);
        }

        public void filterBy(Map<String, List<String>> queryMap,
                             FutureCallback<Response<T>> futureCallback) {
            mRemoteQuery.filterBy(mUrl, queryMap, futureCallback);
        }

        public void get(FutureCallback<Response<T>> futureCallback) {
            mRemoteQuery.get(mUrl, futureCallback);
        }

        public void update(T object, FutureCallback<Response<JsonObject>> futureCallback) {
            mRemoteQuery.update(mUrl, object, futureCallback);
        }

        public void delete(FutureCallback<Response<JsonObject>> futureCallback) {
            mRemoteQuery.delete(mUrl, futureCallback);
        }

    }

}
