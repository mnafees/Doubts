/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

class QueryStore {

    private static QueryStore sInstance;

    private final List<Query> mQueries = new ArrayList<>();

    public void add(Query query) {
        mQueries.add(query);
    }

    public Query get(Context context) {
        for (Query q : mQueries) {
            if (q.getContext().equals(context)) {
                return q;
            }
        }
        return null;
    }

    public void cancelQueryAndRemove(Context context) {
        for (Query q : mQueries) {
            if (q.getContext().equals(context)) {
                Ion.getDefault(q.getContext()).cancelAll();
                Realm realm = Realm.getInstance(q.getContext());
                realm.close();
                mQueries.remove(q);
            }
        }
    }

    public static QueryStore getInstance() {
        synchronized (QueryStore.class) {
            if (sInstance == null) {
                sInstance = new QueryStore();
            }
        }
        return sInstance;
    }

}
