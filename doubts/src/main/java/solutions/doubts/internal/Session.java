/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import io.realm.Realm;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.models.User;
import solutions.doubts.api.query.RemoteQuery;
import solutions.doubts.core.events.SessionUpdatedEvent;

public class Session {

    private static final String TAG = "Session";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private AuthToken mAuthToken;

    public Session(Context context, SharedPreferences preferences) {
        mContext = context;
        mSharedPreferences = preferences;
        int userId = mSharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            mAuthToken = new AuthToken(userId,
                    mSharedPreferences.getString("username", ""),
                    mSharedPreferences.getString("auth_token", ""));
        }
    }

    public AuthToken getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(AuthToken authToken) {
        mAuthToken = authToken;
        mSharedPreferences.edit()
                .putInt("user_id", authToken.getUserId())
                .putString("username", authToken.getUsername())
                .putString("auth_token", authToken.getToken())
                .apply();
    }

    public void getLoggedInUser() {
        if (mAuthToken != null) {
            Realm realm = Realm.getInstance(mContext);
            User user = realm.where(User.class).findFirst();
            if (user == null) {
                Log.d(TAG, "Fetching session user from server ...");
                fetchOrUpdateUser();
            } else {
                Log.d(TAG, "Return local Realm-stored user.");
                DoubtsApplication.getInstance().getBus().post(new
                        SessionUpdatedEvent(user));
            }
        }
    }

    private void fetchOrUpdateUser() {
        RemoteQuery<User> remoteQuery = new RemoteQuery<>(User.class);
        remoteQuery.setContext(mContext);
        remoteQuery.get(mAuthToken.getUserId(), mAuthToken.getUsername())
                .setCallback(new FutureCallback<Response<User>>() {
                    @Override
                    public void onCompleted(Exception e, Response<User> result) {
                        if (result.getHeaders().code() == 200) {
                            Realm realm = Realm.getInstance(mContext);
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(result.getResult());
                            realm.commitTransaction();
                            DoubtsApplication.getInstance().getBus().post(new
                                    SessionUpdatedEvent(result.getResult()));
                        }
                    }
                });
    }

}
