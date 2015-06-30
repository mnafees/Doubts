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
import solutions.doubts.api.query.Query;
import solutions.doubts.core.events.SessionUpdatedEvent;

public class Session {

    private static final String TAG = "Session";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private AuthToken mAuthToken;

    public Session() {
        mContext = DoubtsApplication.getInstance();
        mSharedPreferences = DoubtsApplication.getInstance().getSharedPreferences(
                StringConstants.PREFERENCES_NAME, 0
        );
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
        if (mAuthToken == null) {
            mAuthToken = authToken;
            mSharedPreferences.edit()
                    .putInt("user_id", authToken.getUserId())
                    .putString("username", authToken.getUsername())
                    .putString("auth_token", authToken.getToken())
                    .apply();
        }
    }

    public User getLoggedInUser() {
        if(mAuthToken == null) {
            return null;
        }

        return (User) Query.with(mContext)
                .local(User.class)
                .get(mAuthToken.getUserId());
    }

    public void fetchLoggedInUser() {
        if (mAuthToken != null) {
            User user = (User)Query.with(mContext)
                    .local(User.class)
                    .get(mAuthToken.getUserId());
            if (user == null) {
                Log.d(TAG, "Fetching session user from server ...");
                fetchOrUpdateUser();
            } else {
                if (user.getId() != mSharedPreferences.getInt("user_id", -1)) {
                    // Valid in case a user logged out but his/her details were not
                    // removed from our Realm. This additional check removes the
                    // possibility of such a scenario where our session user is the
                    // last logged-in previous user.
                    Log.d(TAG, "Fetching session user from server ...");
                    fetchOrUpdateUser();
                } else {
                    Log.d(TAG, "Return local Realm-stored user.");
                    DoubtsApplication.getInstance().getBus().post(new
                            SessionUpdatedEvent(user));
                }
            }
        }
    }

    private void fetchOrUpdateUser() {
        Query.with(mContext)
                .remote(User.class)
                .resource("users", mAuthToken.getUserId(), mAuthToken.getUsername())
                .get(new FutureCallback<Response<User>>() {
                    @Override
                    public void onCompleted(Exception e, final Response<User> result) {
                        if (e == null) {
                            if (result.getHeaders().code() == 200) {
                                Log.d(TAG, "Retrieved session user from server");
                                Realm realm = Realm.getInstance(mContext);
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(result.getResult());
                                    }
                                });
                                DoubtsApplication.getInstance().getBus().post(new
                                        SessionUpdatedEvent(result.getResult()));
                            }
                        }
                    }
                });
    }

}
