/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.os.Handler;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import io.realm.RealmObject;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.core.events.NetworkEvent;
import solutions.doubts.core.events.PageableDataEvent;
import solutions.doubts.core.events.ResourceEvent;
import solutions.doubts.internal.RestConstants;

public class Query {

    private static final String TAG = "Query";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private boolean mInitialised;
    private OkHttpClient mOkHttpClient;
    private Gson mGson;
    private Handler mHandler;


    public Query() {
        mInitialised = false;
        DoubtsApplication.getInstance().getBus().register(this);
    }

    public void init() {
        if (mInitialised) {
            Log.d(TAG, "Trying to initialise already initialised instance.");
            return;
        } else {
            mInitialised = true;
        }

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request req = new Request.Builder()
                        .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                                .getInstance().getAuthToken().toString())
                        .url(chain.request().url())
                        .method(chain.request().method(), chain.request().body())
                        .headers(chain.request().headers())
                        .build();
                return chain.proceed(req);
            }
        });
        mGson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return (f.getName().equals("id") && f.getDeclaringClass().equals(int.class)) ||
                               (f.getName().equals("slug") && f.getDeclaringClass().equals(String.class));
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        mHandler = new Handler(DoubtsApplication.getInstance().getMainLooper());
    }

    @Subscribe
    public void onNetworkEvent(final NetworkEvent networkEvent) {
        if (!mInitialised) {
            throw new IllegalStateException("Did you forget to call init()?");
        }

        switch (networkEvent.getOperation()) {
            case CREATE:
                create(networkEvent);
                break;
            case GET:
                get(networkEvent);
                break;
            case GETALL:
                getAll(networkEvent);
                break;
            case UPDATE:
                update(networkEvent);
                break;
            case DELETE:
                delete(networkEvent);
                break;
        }
    }

    private void create(final NetworkEvent networkEvent) {
        final Request req = new Request.Builder()
                .url(networkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, mGson.toJson(
                        networkEvent.getObject(),
                        networkEvent.getClazz())))
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void get(final NetworkEvent networkEvent) {
        final Request req = new Request.Builder()
                .url(networkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .get()
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .jsonObject(mGson.fromJson(
                                            response.body().string(), JsonObject.class
                                    ))
                                    .build()
                    );
                } else {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void getAll(final NetworkEvent networkEvent) {
        final Request req = new Request.Builder()
                .url(networkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .get()
                .build();

        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final JsonObject json = mGson.fromJson(response.body().string(), JsonObject.class);

                final Class clazz = networkEvent.getClazz();
                final String className = clazz.getName().toLowerCase()
                        .substring(
                            clazz.getName().lastIndexOf(".") + 1
                        ) + "s";

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DoubtsApplication.getInstance().getBus().post(
                                PageableDataEvent.newBuilder()
                                        .pageableData(json.getAsJsonArray(className))
                                        .id(networkEvent.getId())
                                        .build()
                        );
                    }
                });
            }
        });
    }

    private void update(final NetworkEvent networkEvent) {
        final Request req = new Request.Builder()
                .url(networkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .put(RequestBody.create(MEDIA_TYPE_JSON, mGson.toJson(
                        networkEvent.getObject(),
                        networkEvent.getClazz())))
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void delete(final NetworkEvent networkEvent) {
        final Request req = new Request.Builder()
                .url(networkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .delete()
                .build();
        mOkHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else {
                    DoubtsApplication.getInstance().getBus().post(
                            ResourceEvent.newBuilder()
                                    .id(networkEvent.getId())
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

}
