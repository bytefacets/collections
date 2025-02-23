package com.bytefacets.collections.hash;

import com.bytefacets.collections.CapacityCalculator;
import com.bytefacets.collections.EntryIterator;
import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.functional.IntConsumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Arrays;
import java.util.ConcurrentModificationException;

public abstract class BaseHash {
    protected static final double DEFAULT_LOAD_FACTOR = 0.75;
    protected static final int MAX_CAPACITY = Integer.MAX_VALUE / 4 + 1; // must be a power of 2

    protected int capacity;
    protected int capThreshold;
    protected double loadFactor;
    protected int[] heads;
    protected int[] nexts;
    protected int size = 0;
    protected int freeList = -1;
    protected int nextUnusedEntry;
    protected int modificationCount;
    protected int maxHead = 0;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    protected BaseHash(final int initialCapacity, final double loadFactor) {
        if (initialCapacity <= 0 || initialCapacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Invalid initialCapacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Invalid loadFactor: " + loadFactor);
        }

        capacity = NumUtils.nextPowerOf2(initialCapacity);

        this.capThreshold = (int) (capacity * loadFactor);
        this.loadFactor = loadFactor;
        this.nextUnusedEntry = 0;

        heads = IntArray.create(capacity, -1);
        nexts = IntArray.create(capacity, -1);
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("FinalParameters")
    public int[] collectEntries(int[] target) {
        if (target == null || target.length < size) {
            target = IntArray.create(size);
        }

        int ptr = 0;
        for (int head = 0; head < heads.length && ptr < size; head++) {
            for (int e = heads[head]; e >= 0; e = nexts[e]) {
                target[ptr++] = e;
            }
        }
        return target;
    }

    protected void calculateNewCapacity() {
        final int newCapacity =
                CapacityCalculator.calculateNewCapacity(this, capacity, capThreshold);
        if (capacity == newCapacity) {
            capThreshold = MAX_CAPACITY;
        } else {
            capacity = newCapacity;
            // apply the load factor to come up with new threshold
            final int newThreshold = (int) (capacity * loadFactor);
            // limit threshold to a MAX of MAX_CAPACITY
            capThreshold = Math.min(newThreshold, MAX_CAPACITY);
        }
    }

    public void clear() {
        if (size > 0) {
            int lim;

            lim = Math.min(heads.length, maxHead + 1);
            Arrays.fill(heads, 0, lim, -1);

            lim = Math.min(nexts.length, nextUnusedEntry);
            Arrays.fill(nexts, 0, lim, -1);

            clearKeys();
            clearValues();
            size = 0;
        }
        modificationCount = 0;
        nextUnusedEntry = 0;
        freeList = -1;
        maxHead = 0;
    }

    @SuppressWarnings("FinalParameters")
    public void forEachEntry(final IntConsumer forEach) {
        if (size == 0) {
            return;
        }
        int hits = 0;
        final int expectedHits = size;
        for (int head = 0; head < heads.length && hits < expectedHits; head++) {
            for (int e = heads[head]; e >= 0; ) {
                final int next = nexts[e]; // pull out next in case the function removes the entry
                hits++;
                forEach.accept(e);
                e = next;
            }
        }
    }

    protected void updateMaxHead(final int head) {
        if (head > maxHead) {
            maxHead = head;
        }
    }

    protected abstract void removeValue(int entry);

    protected abstract void growValues(int size);

    protected abstract void growKeys(int size);

    protected abstract void clearKeys();

    protected abstract void clearValues();

    public EntryIterator iterator() {
        return iterator(null);
    }

    @SuppressWarnings("FinalParameters")
    public EntryIterator iterator(EntryIterator enumerator) {
        if (null == enumerator) {
            enumerator = new IteratorImpl();
        }

        enumerator.reset();
        return enumerator;
    }

    protected void copyFrom(final BaseHash source) {
        if (heads.length != source.heads.length) { // these have to match for the head calc
            heads = IntArray.create(source.heads.length);
        }

        if (nexts.length < source.nexts.length) { // nexts has to accommodate
            nexts = IntArray.create(source.nexts.length);
        } else {
            IntArray.fill(nexts, -1, source.nexts.length, nexts.length - source.nexts.length);
        }

        System.arraycopy(source.heads, 0, heads, 0, source.heads.length);
        System.arraycopy(source.nexts, 0, nexts, 0, source.nexts.length);

        modificationCount++;
        size = source.size;
        loadFactor = source.loadFactor;
        freeList = source.freeList;
        nextUnusedEntry = source.nextUnusedEntry;
        maxHead = source.maxHead;
    }

    protected class IteratorImpl implements EntryIterator {
        protected boolean initialized = false;
        protected boolean isRemoved = false;
        protected int internalModCount = -1;

        protected int head = -1;
        protected int entry = -1;
        protected int prev = -1;
        protected int next = -1;

        protected IteratorImpl() {}

        private void initialize() {
            nextHead();
            initialized = true;
        }

        private void nextHead() {
            for (head++, entry = -1; head < heads.length; head++) {
                entry = heads[head];
                if (entry >= 0) {
                    prev = -1;
                    next = nexts[entry];
                    break;
                }
            }

            this.isRemoved = false;
        }

        @Override
        public void reset() {
            this.head = -1;
            this.entry = -1;
            this.prev = -1;
            this.next = -1;

            this.initialized = false;
            this.isRemoved = false;
            this.internalModCount = modificationCount;
        }

        @Override
        public int currentEntry() {
            return entry;
        }

        @Override
        public boolean next() {
            if (modificationCount != internalModCount) {
                throw new ConcurrentModificationException(
                        getClass() + " was modified outside of Iterator");
            }
            if (!initialized) {
                initialize();
                return entry >= 0;
            }
            // if we have next item in the same bucket
            if (next >= 0) {
                if (!isRemoved) {
                    prev = entry;
                }
                entry = next;
                next = nexts[entry];
                isRemoved = false;
                return true;
            }
            nextHead();
            return entry >= 0;
        }

        @Override
        public void remove() {
            if (modificationCount != internalModCount) {
                throw new ConcurrentModificationException(
                        getClass() + " was modified outside of Iterator");
            }

            if (isRemoved) {
                throw new IllegalStateException();
            }

            if (!initialized) {
                initialize();
            }

            next = nexts[entry];
            if (prev < 0) {
                heads[head] = next; // new head
            } else {
                nexts[prev] = next;
            }

            // released entry becomes head of free list
            nexts[entry] = freeList;
            freeList = entry;
            size--;

            removeValue(entry);
            isRemoved = true;

            internalModCount++;
            modificationCount++;
        }
    }
}
