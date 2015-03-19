/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import com.google.gson.JsonObject;

public class ResourceEvent {

    public enum Type {
        SUCCESS,
        FAILURE
    }

    private int mId; // the id of the network event that caused this event
    private Type mType;
    private JsonObject mJsonObject; // in case of a successful GET operation

    private ResourceEvent() {}

    public int getId() {
        return mId;
    }

    public Type getType() {
        return mType;
    }

    public JsonObject getJsonObject() {
        return mJsonObject;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private ResourceEvent mResourceEvent;

        private Builder() {
            mResourceEvent = new ResourceEvent();
        }

        public Builder id(final int id) {
            mResourceEvent.mId = id;
            return this;
        }

        public Builder type(final Type type) {
            mResourceEvent.mType = type;
            return this;
        }

        public Builder jsonObject(final JsonObject jsonObject) {
            mResourceEvent.mJsonObject = jsonObject;
            return this;
        }

        public ResourceEvent build() {
            return mResourceEvent;
        }

    }

}
