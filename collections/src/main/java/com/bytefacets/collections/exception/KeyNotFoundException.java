package com.bytefacets.collections.exception;

public final class KeyNotFoundException extends RuntimeException {
    public KeyNotFoundException(final byte key) {
        super("Byte Key not found: " + key);
    }

    public KeyNotFoundException(final short key) {
        super("Short Key not found: " + key);
    }

    public KeyNotFoundException(final char key) {
        super("Char Key not found: " + key);
    }

    public KeyNotFoundException(final int key) {
        super("Int Key not found: " + key);
    }

    public KeyNotFoundException(final long key) {
        super("Long Key not found: " + key);
    }

    public KeyNotFoundException(final double key) {
        super("Double Key not found: " + key);
    }

    public KeyNotFoundException(final String key) {
        super("String Key not found: " + key);
    }

    public KeyNotFoundException(final Object key) {
        super("Object Key not found: " + key);
    }
}
