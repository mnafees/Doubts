package solutions.doubts.api.backend;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import solutions.doubts.api.models.Question;

public interface QuestionApi {
    @GET("/questions/{id}/{slug}")
    Observable<Question> get(@Path("id") int id, @Path("slug") String slug);

    @POST("/questions")
    void save(@Body Question question, @Path("id") int id, @Path("slug") String slug);
}
