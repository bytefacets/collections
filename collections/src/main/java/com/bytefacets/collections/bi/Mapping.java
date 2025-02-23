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

    public int count() {
        return side.count(key);
    }

    public void forEachValue(final IntConsumer consumer) {
        valueIterator().forEach(consumer);
    }

    public void forEachEntry(final IntConsumer consumer) {
        entryIterator().forEach(consumer);
    }

    public IntIterator entryIterator() {
        entryIterator.reset(key);
        return entryIterator;
    }

    public IntIterator valueIterator() {
        valueIterator.reset(key);
        return valueIterator;
    }
}
