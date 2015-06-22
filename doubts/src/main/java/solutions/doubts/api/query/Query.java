/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import solutions.doubts.api.ServerResponseCallback;

public class Query {

    public enum Order {
        desc,
        asc
    }

    private Context mContext;

    private Query(Context context) {
        mContext = context;
    }

    @SuppressWarnings("unchecked")
    public RemoteQueryCallbackBuilder remote(Class<?> clazz) {
        return new RemoteQueryCallbackBuilder(new RemoteQuery(clazz, mContext));
    }

    @SuppressWarnings("unchecked")
    public LocalQuery local(Class<?> clazz) {
        return new LocalQuery(clazz, mContext);
    }

    public static Query with(Context context) {
        return new Query(context);
    }

    public static class RemoteQueryCallbackBuilder {

        private RemoteQuery mRemoteQuery;

        private RemoteQueryCallbackBuilder(RemoteQuery remoteQuery) {
            mRemoteQuery = remoteQuery;
        }

        @SuppressWarnings("unchecked")
        public RemoteQuery setServerResponseCallback(ServerResponseCallback<?> callback) {
            mRemoteQuery.setServerResponseCallback(callback);
            return mRemoteQuery;
        }

    }

}
