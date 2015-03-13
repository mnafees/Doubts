/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import com.squareup.okhttp.OkHttpClient;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.internal.RestConstants;

public class RestAdapterUtil {

    private static RestAdapter restAdapter;

    public static RestAdapter getRestAdapter() {
        synchronized (RestAdapter.class) {
            if (restAdapter == null) {
                restAdapter = new RestAdapter.Builder()
                        .setEndpoint(RestConstants.API_ENDPOINT + "/api/v1")
                        .setClient(new OkClient(new OkHttpClient()))
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader(RestConstants.HEADER_AUTHORIZATION,
                                        DoubtsApplication.getInstance().getAuthToken().toString());
                            }
                        })
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setErrorHandler(new ErrorHandler() {
                            @Override
                            public Throwable handleError(RetrofitError cause) {
                                if (cause.getResponse().getStatus() == 401) {
                                    // @TODO: how to deal with this?
                                }
                                return cause;
                            }
                        })
                        .build();
            }
        }
        return restAdapter;
    }

}
