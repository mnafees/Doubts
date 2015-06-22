/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class LocalQuery<T extends RealmObject> {

    private static final String TAG = "LocalQuery";

    private Realm mRealm;
    private Class<T> mClazz;

    LocalQuery(Class<T> clazz, Context context) {
        mClazz = clazz;
        mRealm = Realm.getInstance(context);
    }

    public void create(final T object) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(object);
            }
        });
    }

    public List<T> getAll(Query.Order order, String sort, int offset) {
        if (order == null) order = Query.Order.desc;
        if (sort == null) sort = "id";
        RealmResults<T> results = mRealm.where(mClazz).findAllSorted(sort,
                order == Query.Order.asc);
        return results.subList(offset, results.size());

    }

    public List<T> filterBy(String parameter, String value) {
        RealmResults<T> results = mRealm.where(mClazz).findAll();
        RealmQuery<T> query = results.where().contains(parameter, value);
        return query.findAll();

    }

    public void filterBy(Map<String, List<String>> queryMap) {
        /** FIXME: how to implement? */
    }

    public T get(int id) {
        RealmResults<T> results = mRealm.where(mClazz).findAll();
        return results.where().equalTo("id", id).findFirst();
    }

    public void update(T object) {
        create(object);
    }

    public void delete(int id) {
        RealmResults<T> results = mRealm.where(mClazz).findAll();
        final T object = results.where().equalTo("id", id).findFirst();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                object.removeFromRealm();
            }
        });
    }

}
