/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

public class ConnectivityChangedEvent {

    private boolean mConnected;

    public ConnectivityChangedEvent(final boolean isConnected) {
        mConnected = isConnected;
    }

    public boolean isConnected() {
        return mConnected;
    }

}
