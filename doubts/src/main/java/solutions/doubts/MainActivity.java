/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */

package solutions.doubts;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;

import solutions.doubts.activities.login.LoginActivity;

/**
 * The base activity of the app.
 */
public class MainActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Notification n = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_doubts)
                .setColor(getResources().getColor(R.color.primary))
                .setContentTitle("Doubts").build();
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.notify(0, n);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        /*setContentView(R.layout.layout_card);
        RelativeTimeTextView timeTextView = (RelativeTimeTextView)findViewById(R.id.timestamp);
        timeTextView.setReferenceTime(new Date().getTime());
        HListView tagList = (HListView)findViewById(R.id.tagList);
        List<String> tags = new ArrayList<>();
        tags.add("#programming");
        tags.add("#java");
        tags.add("#android");
        tags.add("#nullpointerexception");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_single_tag,
                R.id.tag, tags);
        tagList.setAdapter(arrayAdapter);*/
        //setContentView(R.layout.layout_question);
    }

}
