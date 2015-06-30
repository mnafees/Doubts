/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import io.realm.RealmObject;

public class Query {

    public enum Order {
        desc,
        asc
    }

    private Context mContext;

    private Query(Context context) {
        mContext = context;
    }

    public <T>RemoteQuery.Builder<T> remote(Class<T> clazz) {
        return new RemoteQuery.Builder<>(
                clazz, mContext
        );
    }

    public <T extends RealmObject>LocalQuery local(Class<T> clazz) {
        return new LocalQuery<>(clazz, mContext);
    }

    public void cancelAll() {
        QueryStore.getInstance().cancelQueryAndRemove(mContext);
    }

    Context getContext() {
        return mContext;
    }

    public static Query with(Context context) {
        Query query = QueryStore.getInstance().get(context);
        if (query == null) {
            query = new Query(context);
            QueryStore.getInstance().add(query);
        }
        return query;
    }

}
