package solutions.doubts;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import solutions.doubts.activities.questionview.QuestionViewActivity;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.User;
import solutions.doubts.services.DoubtsGcmListenerService;

public class NotificationUtility {
    private static NotificationCompat.Builder buildBasicNotification(Context context) {
        int primaryColor = context.getResources().getColor(R.color.primary);
        return new NotificationCompat.Builder(context)
                .setColor(primaryColor)
                .setSmallIcon(R.drawable.ic_stat_doubts)
                .setLights(primaryColor, 1000, 1000)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL);
    }

    public static Notification makeQuestionNewAnswer(Context context, Answer answer) {
        Intent i = new Intent(context, QuestionViewActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        return buildBasicNotification(context)
                .setContentTitle(context.getString(R.string.question_new_answer_title))
                .setContentText(answer.getTitle())
                .setContentIntent(pi)
                .build();
    }

    // TODO FIXME: Fix intents here -- can only be done when ProfileActivity is fixed.
    public static Notification makeUserNewFollower(Context context, User user) {
        Intent i = new Intent(context, QuestionViewActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        String ident = user.getName();
        if(ident == null)
            ident = user.getUsername();
        return buildBasicNotification(context)
                .setContentTitle(String.format(context.getString(R.string.user_new_profile_title), ident))
                .addAction(R.drawable.ic_person_add_white_24dp, context.getString(R.string.follow_back), pi)
                .setContentIntent(pi)
                .build();
    }
}
