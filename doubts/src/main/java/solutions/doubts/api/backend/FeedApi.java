package solutions.doubts.api.backend;

import com.google.gson.JsonObject;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;
import solutions.doubts.api.models.AuthToken;
import solutions.doubts.internal.RestConstants;


public interface FeedApi {
    @GET("/feed")
    public Observable<JsonObject> get(@Header(RestConstants.HEADER_AUTHORIZATION) AuthToken token);
}
