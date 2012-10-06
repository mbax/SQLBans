package org.kitteh.sqlbans.exceptions;

public class SQLBansThreadingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SQLBansThreadingException() {
        super("Detected unsafe method call");
    }
}
