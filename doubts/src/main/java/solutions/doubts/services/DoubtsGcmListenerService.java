package solutions.doubts.services;

import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import io.realm.Realm;
import solutions.doubts.NotificationUtility;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.User;

public class DoubtsGcmListenerService extends GcmListenerService {
    public static final String EVENT_QUESTION_NEW_ANSWER = "solutions.doubts.events.question_new_answer";
    public static final String EVENT_USER_NEW_FOLLOWER = "solutions.doubts.events.user_new_follower";

    private static final String EVENT = "event";
    private static final String ANSWER = "answer";
    private static final String USER = "user";

    private NotificationManagerCompat notificationManager;
    private final Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String event = data.getString(EVENT);
        if(EVENT_QUESTION_NEW_ANSWER.equals(event)) {
            Answer answer = gson.fromJson(data.getString(ANSWER), Answer.class);
            notificationManager.notify(0, NotificationUtility.makeQuestionNewAnswer(this, answer));
        } else if(EVENT_USER_NEW_FOLLOWER.equals(event)) {
            User user = gson.fromJson(data.getString(USER), User.class);
            notificationManager.notify(0, NotificationUtility.makeUserNewFollower(this, user));
        }
    }
}
