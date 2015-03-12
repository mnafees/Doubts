/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <aviraldg@gmail.com>.
 */

package solutions.doubts.api.backend;

import com.google.gson.JsonObject;

import retrofit.http.GET;
import rx.Observable;

public interface FeedApi {

    @GET("/feed")
    public Observable<JsonObject> get();

}
