/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import java.util.Random;

import solutions.doubts.DoubtsApplication;

public class NetworkEvent {

    public enum Operation {
        CREATE,
        GET,
        GETALL,
        UPDATE,
        DELETE
    }

    private int mId; // generated automatically
    private String mUrl;
    private Class mClazz;
    private Operation mOperation;
    private Object mObject;

    private NetworkEvent() {}

    public int getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public Class getClazz() {
        return mClazz;
    }

    public Operation getOperation() {
        return mOperation;
    }

    public Object getObject() {
        return mObject;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private NetworkEvent mNetworkEvent;

        private Builder() {
            mNetworkEvent = new NetworkEvent();
        }

        public Builder url(final String url) {
            mNetworkEvent.mUrl = url;
            return this;
        }

        public Builder clazz(final Class clazz) {
            mNetworkEvent.mClazz = clazz;
            return this;
        }

        public Builder operation(final Operation operation) {
            mNetworkEvent.mOperation = operation;
            return this;
        }

        // used in case of CREATE and UPDATE operations
        public Builder object(final Object object) {
            mNetworkEvent.mObject = object;
            return this;
        }

        public NetworkEvent build() {
            // randomly generate an ID for this event
            mNetworkEvent.mId = new Random().nextInt();
            return mNetworkEvent;
        }

    }

    public void post() {
        DoubtsApplication.getInstance().getBus().post(this);
    }

}