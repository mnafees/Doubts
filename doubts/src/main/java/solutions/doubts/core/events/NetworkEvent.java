/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import java.util.Random;

public class NetworkEvent {

    public enum Operation {
        CREATE,
        GET,
        GETALL,
        UPDATE,
        DELETE
    }

    private String mUrl;
    private Class mClazz;
    private Operation mOperation;
    private Object mObject;

    private NetworkEvent() {}

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
            return mNetworkEvent;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkEvent that = (NetworkEvent) o;

        if (!mClazz.equals(that.mClazz)) return false;
        if (!mObject.equals(that.mObject)) return false;
        if (mOperation != that.mOperation) return false;
        if (!mUrl.equals(that.mUrl)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = new Random().nextInt();
        result = 31 * result + mUrl.hashCode();
        result = 31 * result + mClazz.hashCode();
        result = 31 * result + mOperation.hashCode();
        result = 31 * result + mObject.hashCode();
        return result;
    }
}