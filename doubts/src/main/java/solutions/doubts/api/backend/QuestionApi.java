/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Aviral Dasgupta (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.backend;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import solutions.doubts.api.models.Question;
import solutions.doubts.internal.RestConstants;

public interface QuestionApi {

    @GET("/questions/{id}/{slug}")
    Observable<Question> get(@Path("id") int id,
                             @Path("slug") String slug);

    @POST("/questions")
    void save(@Header(RestConstants.HEADER_AUTHORIZATION) String authHeader,
              @Body Question question);

}
