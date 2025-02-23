package com.bytefacets.collections.exception;

public class ResizeException extends RuntimeException {
    public static ResizeException cannotResize(
            final Class<?> ownerType,
            final String resizeObjectType,
            final int currentSize,
            final int attemptedSize) {
        return new ResizeException(
                String.format(
                        "%s failed resize attempt of %s from %d to %d",
                        ownerType.getName(), resizeObjectType, currentSize, attemptedSize));
    }

    public ResizeException(final String message) {
        super(message);
    }
}
