// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

import static com.bytefacets.collections.bi.CompactOneToMany.UNSET;

import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.functional.IntIterator;

final class Side {
    private int[] heads;
    private int[] nexts;
    private int[] prevs;
    private int[] count;

    Side(final int initialCapacity, final int initialMappingCapacity, final boolean includeCounts) {
        heads = IntArray.create(initialCapacity, UNSET);
        nexts = IntArray.create(initialMappingCapacity, UNSET);
        prevs = IntArray.create(initialMappingCapacity, UNSET);
        count = includeCounts ? IntArray.create(initialCapacity) : null;
    }

    int count(final int key) {
        if (count == null) {
            throw new IllegalStateException("Counts are not tracked in this collection");
        }
        return key < count.length ? count[key] : 0;
    }

    void insertEntry(final int key, final int entry) {
        ensureKey(key);
        final int oldHead = heads[key];
        if (oldHead != UNSET) {
            ensureEntry(entry);
            nexts[entry] = oldHead; // prepend
            prevs[oldHead] = entry;
        }
        heads[key] = entry; // always gets the new head
        if (count != null) {
            count[key]++;
        }
    }

    @SuppressWarnings("NeedBraces")
    void removeEntry(final int key, final int entry) {
        if (heads[key] == entry) {
            heads[key] = nexts[entry];
        } else {
            final int prev = prevs[entry];
            final int next = nexts[entry];
            if (prev != UNSET) nexts[prev] = next;
            if (next != UNSET) prevs[next] = prev;
        }
        prevs[entry] = UNSET;
        nexts[entry] = UNSET;
        if (count != null) {
            count[key]--;
        }
    }

    private void ensureKey(final int key) {
        heads = IntArray.ensureEntry(heads, key, UNSET);
        if (count != null) {
            count = IntArray.ensureEntry(count, key);
        }
    }

    private void ensureEntry(final int entry) {
        nexts = IntArray.ensureEntry(nexts, entry, UNSET);
        prevs = IntArray.ensureEntry(prevs, entry, UNSET);
    }

    ValueIterator createValueIterator(final CompactOneToMany.EntryLookupFunc lookupFunc) {
        return new ValueIterator(lookupFunc);
    }

    EntryIterator createEntryIterator() {
        return new EntryIterator();
    }

    public final class EntryIterator implements IntIterator {
        private int key;
        private int entry;

        private EntryIterator() {}

        void reset(final int key) {
            this.key = key;
            reset();
        }

        public void reset() {
            if (key >= 0 && key < heads.length) {
                this.entry = heads[key];
            } else {
                this.entry = UNSET;
            }
        }

        @Override
        public boolean hasNext() {
            return entry != UNSET;
        }

        @Override
        public int next() {
            final int result = entry;
            entry = nexts[entry];
            return result;
        }
    }

    public final class ValueIterator implements IntIterator {
        private final CompactOneToMany.EntryLookupFunc lookupFunc;
        private int key;
        private int entry;

        private ValueIterator(final CompactOneToMany.EntryLookupFunc lookupFunc) {
            this.lookupFunc = lookupFunc;
        }

        void reset(final int key) {
            this.key = key;
            reset();
        }

        public void reset() {
            this.entry = heads[key];
        }

        @Override
        public boolean hasNext() {
            return entry != UNSET;
        }

        @Override
        public int next() {
            final int value = lookupFunc.getAt(entry);
            entry = nexts[entry];
            return value;
        }
    }
}
