/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.api.question.retrofit;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import solutions.doubts.core.api.question.domain.Question;

public interface RetrofitQuestionService {

    @GET("/questions/{id}/{slug}")
    public Question fetchQuestion(@Path("id") final int id,
                                  @Path("slug") final String slug);

    @POST("/questions")
    public Question createNewQuestion(@Body final Question question);

    @PUT("/questions/{id}/{slug}")
    public Question updateQuestion(@Path("id") final int id,
                                   @Path("slug") final String slug,
                                   @Body final Question question);

    @GET("/questions")
    public JsonObject fetchQuestionResource(@QueryMap Map<String, Object> queryMap);

}
