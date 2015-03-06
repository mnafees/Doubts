/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.authentication.retrofit;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface RetrofitAuthenticationService {

    @FormUrlEncoded
    @POST("/auth/login")
    public Observable<Response> login(@Field("email") final String email);

    @FormUrlEncoded
    @POST("/auth/register")
    public Observable<Response> register(@Query("email") final String encodedEmail,
                                         @Field("email") final String email,
                                         @Field("username") final String username,
                                         @Field("name") final String name);

}
