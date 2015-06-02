/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import java.util.HashMap;

class QueryPool {

    private static final HashMap<Context, Query> sContextQueries = new HashMap<>();

    public static Query getQueryForContext(final Context context) {
        if (sContextQueries.containsKey(context)) {
            return sContextQueries.get(context);
        } else {
            Query query = new Query();
            sContextQueries.put(context, query);
            return query;
        }
    }

}
