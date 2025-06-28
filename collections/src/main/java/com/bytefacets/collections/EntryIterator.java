// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections;

public interface EntryIterator {
    void reset();

    int currentEntry();

    void remove();

    boolean next();
}
