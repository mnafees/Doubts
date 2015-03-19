/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import com.google.gson.JsonArray;

public class PageableDataEvent {

    private int mId; // the id of the network event that caused this event
    private JsonArray mPageableData;

    private PageableDataEvent() {}

    public int getId() {
        return mId;
    }

    public JsonArray getPageableData() {
        return mPageableData;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private PageableDataEvent mEvent;

        private Builder() {
            mEvent = new PageableDataEvent();
        }

        // the id of the network event that caused this event
        public Builder id(final int id) {
            mEvent.mId = id;
            return this;
        }

        public Builder pageableData(final JsonArray data) {
            mEvent.mPageableData = data;
            return this;
        }

        public PageableDataEvent build() {
            return mEvent;
        }

    }

}
