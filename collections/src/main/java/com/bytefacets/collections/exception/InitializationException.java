// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.exception;

public final class InitializationException extends RuntimeException {
    private InitializationException(final String message) {
        super(message);
    }

    public static void assertMinimum(final int minValid, final int value, final String field) {
        if (value < minValid) {
            throw new InitializationException(
                    String.format("%s must be >= %d, but was %d", field, minValid, value));
        }
    }
}
