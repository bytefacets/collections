// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.exception;

public class InvalidEntryException extends RuntimeException {
    public InvalidEntryException(final String message) {
        super(message);
    }

    public static InvalidEntryException invalidEntry(final int entry) {
        return new InvalidEntryException("Invalid entry: " + entry);
    }
}
