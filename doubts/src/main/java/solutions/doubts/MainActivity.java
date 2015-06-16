/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */

package solutions.doubts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import solutions.doubts.activities.authentication.AuthenticationActivity;
import solutions.doubts.activities.feed.FeedActivity;
import solutions.doubts.internal.AuthToken;

/**
 * The base activity of the app.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        final Account[] list = manager.getAccounts();
        String gmail = "";
        for (final Account account: list) {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                gmail = account.name;
                break;
            }
        }
        gmail = "nafees.technocool@gmail.com";
        final List<String> allowedEmailsList = new LinkedList<>();
        final String[] allowedEmails = {"nafees.technocool@gmail.com", "aviraldg@gmail.com", "nafisa.zainab0195@gmail.com",
                "anirban.loyola@gmail.com", "kishalayraj@gmail.com", "aakashsharma2370@gmail.com"};
        Collections.addAll(allowedEmailsList, allowedEmails);
        if (allowedEmailsList.contains(gmail)) {
            if (getIntent().getData() != null) {
                final Uri data = getIntent().getData();
                if ((data.getScheme().equals("http") ||
                        data.getScheme().equals("https")) &&
                        data.getPath().contains("/auth/token/")) {
                    final int length = data.getPathSegments().size();
                    if (length != 5) {
                        // mischief alert!
                        Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                        startAuthenticationActivity();
                    } else {
                        try {
                            final int id = Integer.valueOf(data.getPathSegments().get(length - 3));
                            final String username = data.getPathSegments().get(length - 2);
                            final String authToken = data.getPathSegments().get(length - 1);

                            DoubtsApplication.getInstance().setAuthToken(
                                    new AuthToken(id, username, authToken)
                            );

                            startFeedActivity();
                        } catch (NumberFormatException e) {
                            // mischief alert!
                            Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                            startAuthenticationActivity();
                        }
                    }
                }
            } else {
                if (DoubtsApplication.getInstance().getAuthToken() != null) {
                    startFeedActivity();
                } else {
                    startAuthenticationActivity();
                }
            }
        } else {
            Toast.makeText(this, "You're not authorised to use this app.", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void startAuthenticationActivity() {
        final Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }

    private void startFeedActivity() {
        final Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

}
