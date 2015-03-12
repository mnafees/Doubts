package solutions.doubts.api.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;
import solutions.doubts.api.backend.FeedApi;

public class Feed implements Serializable {
    private static FeedApi mFeedApiImpl;
    private static Gson mGson = new Gson();

    private LinkedList<Question> mFeedItems;

    public static void setRestAdapter(RestAdapter restAdapter) {
        mFeedApiImpl = restAdapter.create(FeedApi.class);
    }

    public Feed(AuthToken authToken) {
        mFeedItems = new LinkedList<Question>();
    }

    public Observable<Feed> fetchNext() {
        return mFeedApiImpl.get().map(new Func1<JsonObject, Feed>() {
            @Override
            public Feed call(JsonObject responseJson) {
                Question [] questions = mGson.fromJson(responseJson.getAsJsonArray("questions"), Question[].class);
                Collections.addAll(Feed.this.mFeedItems, questions);
                return Feed.this;
            }
        });
    }

    public void reset() {
        mFeedItems.clear();
    }

    public int getLength() {
        return mFeedItems.size();
    }

    public Question getItem(int index) {
        return mFeedItems.get(index);
    }
}
