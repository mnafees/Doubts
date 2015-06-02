/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import com.google.gson.JsonArray;

public class PageableDataEvent {

    private JsonArray mPageableData;

    private PageableDataEvent() {}

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

        public Builder pageableData(final JsonArray data) {
            mEvent.mPageableData = data;
            return this;
        }

        public PageableDataEvent build() {
            return mEvent;
        }

    }

}
