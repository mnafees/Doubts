/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.events;

import com.google.gson.JsonObject;

public class AuthenticatedEvent {

    private JsonObject jsonObject;

    public AuthenticatedEvent(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

}
