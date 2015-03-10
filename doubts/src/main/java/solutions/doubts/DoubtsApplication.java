/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts;

import android.app.Application;
import android.content.Context;

public class DoubtsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static DoubtsApplication get(Context context) {
        return (DoubtsApplication)context.getApplicationContext();
    }

}
