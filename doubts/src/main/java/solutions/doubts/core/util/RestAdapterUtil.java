/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

public class RestAdapterUtil {

    private static RestAdapter restAdapter;

    public static RestAdapter getRestAdapter() {
        synchronized (RestAdapter.class) {
            if (restAdapter == null) {
                restAdapter = new RestAdapter.Builder()
                        .setEndpoint("http://192.168.1.9:8080/api/v1")
                        .setClient(new OkClient(new OkHttpClient()))
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setErrorHandler(new ErrorHandler() {
                            @Override
                            public Throwable handleError(RetrofitError cause) {
                                Log.d("RestAdapterUtil", cause.getMessage());
                                return cause;
                            }
                        })
                        .build();
            }
        }
        return restAdapter;
    }

}
