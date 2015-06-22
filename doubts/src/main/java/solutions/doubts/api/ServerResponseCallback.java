/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api;

public interface ServerResponseCallback<T> {

    void onCompleted(Exception e, T result);

}
