/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import solutions.doubts.DoubtsApplication;

public class ServerReponse<T> implements FutureCallback<Response<JsonObject>> {

    private Class mClazz;
    private ServerResponseCallback<T> mCallback;

    public void setClazz(Class clazz) {
        mClazz = clazz;
    }

    public void setServerResponseCallback(ServerResponseCallback<T> callback) {
        mCallback = callback;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCompleted(Exception e, Response<JsonObject> result) {
        if (e != null) {
            mCallback.onCompleted(e, null);
        } else {
            if (result != null) {
                if (result.getHeaders().code() == 200) {
                    T object = (T)DoubtsApplication.getInstance().getGson().fromJson(
                            result.getResult(), mClazz
                    );
                    mCallback.onCompleted(null, object);
                } else {
                    String message = result.getResult().get("message").getAsString();
                    switch (result.getHeaders().code()) {
                        case 401:
                            DoubtsApplication.getInstance().logout(message);
                            break;
                    }
                }
            }
        }
    }

}
