/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import com.google.gson.JsonObject;

public class ResourceEvent {

    public enum Type {
        SUCCESS,
        FAILURE,
        UNAUTHORISED
    }

    private Type mType;
    private JsonObject mJsonObject; // in case of a successful GET operation

    private ResourceEvent() {}

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
