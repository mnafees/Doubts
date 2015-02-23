/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.exceptions;

public class LoginException extends RuntimeException {

    public enum Type {
        USER_DOES_NOT_EXIST
    }

    private Type type;

    public LoginException(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

}
