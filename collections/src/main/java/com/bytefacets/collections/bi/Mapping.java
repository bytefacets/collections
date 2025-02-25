// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

import static java.util.Objects.requireNonNull;

import com.bytefacets.collections.functional.IntConsumer;
import com.bytefacets.collections.functional.IntIterator;

public final class Mapping {
    private final Side side;
    private final Side.EntryIterator entryIterator;
    private final Side.ValueIterator valueIterator;
    private int key = CompactOneToMany.UNSET;

    Mapping(final Side side, final CompactOneToMany.EntryLookupFunc valueLookupFunc) {
        this.side = requireNonNull(side, "side");
        this.entryIterator = side.createEntryIterator();
        this.valueIterator = side.createValueIterator(valueLookupFunc);
    }

    void resetKey(final int key) {
        this.key = key;
    }

    /**
     * The count of values associated with the given key.
     *
     * @throws IllegalArgumentException if counts were not enabled in the parent collection's
     *     constructor
     */
    public int count() {
        return side.count(key);
    }

    /** Iterate the values associated with the key. */
    public void forEachValue(final IntConsumer consumer) {
        valueIterator().forEach(consumer);
    }

    /** Iterate the entries associated with the key. */
    public void forEachEntry(final IntConsumer consumer) {
        entryIterator().forEach(consumer);
    }

    /** Iterate the entries associated with the key. */
    public IntIterator entryIterator() {
        entryIterator.reset(key);
        return entryIterator;
    }

    /** Iterate the values associated with the key. */
    public IntIterator valueIterator() {
        valueIterator.reset(key);
        return valueIterator;
    }
}
