// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.exception;

public final class RangeCheckException extends RuntimeException {
    private RangeCheckException(final String message) {
        super(message);
    }

    public static void assertWithinRange(
            final int min, final int max, final int value, final String field) {
        if (value < min || value > max) {
            throw new RangeCheckException(
                    String.format(
                            "%s value must be between %d and %d, but was %d",
                            field, min, max, value));
        }
    }
}
