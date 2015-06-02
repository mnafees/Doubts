/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.query;

import android.content.Context;
import android.os.Handler;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;

import io.realm.RealmObject;
import solutions.doubts.DoubtsApplication;

public class Query {

    private static final String TAG = "Query";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private Gson mGson;
    private Handler mHandler;

    Query() {
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
                        return (f.getName().equals("id")) ||
                                (f.getName().equals("slug") && f.getDeclaringClass() == String.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        mHandler = new Handler(DoubtsApplication.getInstance().getMainLooper());
    }

    public static Query with(final Context context) {
        return QueryPool.getQueryForContext(context);
    }

    public void start() {
        /*if (mNetworkEvent == null) {
            throw new IllegalStateException("start() called without assigning a NetworkEvent");
        }
        if (mCallback == null) {
            throw new IllegalStateException("start() called without assigning a QueryCallback");
        }
        switch (mNetworkEvent.getOperation()) {
            case CREATE:
                create();
                break;
            case GET:
                get();
                break;
            case GETALL:
                getAll();
                break;
            case UPDATE:
                update();
                break;
            case DELETE:
                delete();
                break;
        }*/
    }

    /*private void create() {
        final Request req = new Request.Builder()
                .url(mNetworkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .post(RequestBody.create(MEDIA_TYPE_JSON, mGson.toJson(
                        mNetworkEvent.getObject(),
                        mNetworkEvent.getClazz())))
                .build();
        DoubtsApplication.getInstance().getOkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else if (response.code() == 401) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.UNAUTHORISED)
                                    .build()
                    );
                } else {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void get() {
        final Request req = new Request.Builder()
                .url(mNetworkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .get()
                .build();
        DoubtsApplication.getInstance().getOkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(e);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        final JsonObject jsonObject = mGson.fromJson(response.body().string(), JsonObject.class);
                        mCallback.onSuccess(
                                ResourceEvent.newBuilder()
                                        .type(ResourceEvent.Type.SUCCESS)
                                        .jsonObject(jsonObject)
                                        .build()
                        );
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                } else if (response.code() == 401) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.UNAUTHORISED)
                                    .build()
                    );
                } else {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void getAll() {
        final Request req = new Request.Builder()
                .url(mNetworkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .get()
                .build();

        DoubtsApplication.getInstance().getOkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(e);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final JsonObject json = mGson.fromJson(response.body().string(), JsonObject.class);

                final Class clazz = mNetworkEvent.getClazz();
                final String className = clazz.getName().toLowerCase()
                        .substring(
                            clazz.getName().lastIndexOf(".") + 1
                        ) + "s";

                mCallback.onSuccess(
                        PageableDataEvent.newBuilder()
                                .pageableData(json.getAsJsonArray(className))
                                .build()
                );
            }
        });
    }

    private void update() {
        final Request req = new Request.Builder()
                .url(mNetworkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .put(RequestBody.create(MEDIA_TYPE_JSON, mGson.toJson(
                        mNetworkEvent.getObject(),
                        mNetworkEvent.getClazz())))
                .build();

        DoubtsApplication.getInstance().getOkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else if (response.code() == 401) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.UNAUTHORISED)
                                    .build()
                    );
                } else {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }

    private void delete() {
        final Request req = new Request.Builder()
                .url(mNetworkEvent.getUrl())
                .addHeader(RestConstants.HEADER_AUTHORIZATION, DoubtsApplication
                        .getInstance().getAuthToken().toString())
                .delete()
                .build();

        DoubtsApplication.getInstance().getOkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.SUCCESS)
                                    .build()
                    );
                } else if (response.code() == 401) {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.UNAUTHORISED)
                                    .build()
                    );
                } else {
                    mCallback.onSuccess(
                            ResourceEvent.newBuilder()
                                    .type(ResourceEvent.Type.FAILURE)
                                    .build()
                    );
                }
            }
        });
    }*/

}
