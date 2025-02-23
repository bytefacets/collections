package com.bytefacets.collections.queue;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.functional.${type.name}Iterator;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.NoSuchElementException;

import static com.bytefacets.collections.exception.ResizeException.cannotResize;
import static com.bytefacets.collections.exception.InvalidEntryException.invalidEntry;
import static com.bytefacets.collections.CapacityCalculator.calculateNewCapacity;

/**
 * This is a generated class. Changes to the class should be made on the template and the generator re-run.
 */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}HydraDeque${generics} {
    private ${type.arrayType}[] values;
    private int[] heads;
    private int[] tails;
    private int[] count;

    private int[] nexts;
    private int[] prevs;
    private int freeList = -1;
    private int size = 0;
    private int maxEntry = -1;

    public ${type.name}HydraDeque(final int initialQueues, final int initialCapacity) {
        if(initialCapacity < 1) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        final int pow2Capacity = NumUtils.nextPowerOf2(initialCapacity);
        heads = IntArray.create(initialQueues, -1);
        tails = IntArray.create(initialQueues, -1);
        count = IntArray.create(initialQueues);

        nexts = IntArray.create(pow2Capacity, -1);
        prevs = IntArray.create(pow2Capacity, -1);
        values = ${type.name}Array.create(pow2Capacity);
    }

    private void ensureList(final int listId) {
        heads = IntArray.ensureEntry(heads, listId, -1);
        tails = IntArray.ensureEntry(tails, listId, -1);
        count = IntArray.ensureEntry(count, listId);
    }

    private void addToFreeList(final int entry) {
        nexts[entry] = freeList;
        freeList = -entry - 2;
    }

    private int allocateEntry() {
        final int entry;
        if(freeList < -1) {
            entry = -(freeList + 2);
            freeList = nexts[entry];
        } else {
            entry = ++maxEntry;
            values = ${type.name}Array.ensureEntry(values, entry);
            nexts = IntArray.ensureEntry(nexts, entry, -1);
            prevs = IntArray.ensureEntry(prevs, entry, -1);
        }
        return entry;
    }

    public int addFirst(final int listId, final ${type.javaType} value) {
        ensureList(listId);
        final int oldHead = heads[listId];
        final int entry = allocateEntry();

        values[entry] = value;
        heads[listId] = entry;
        count[listId]++;
        if(oldHead == -1) {
            tails[listId] = entry;
        } else {
            prevs[oldHead] = entry;
        }

        nexts[entry] = oldHead;
        size++;
        return entry;
    }

    public int addLast(final int listId, final ${type.javaType} value) {
        ensureList(listId);
        final int oldTail = tails[listId];
        final int entry = allocateEntry();

        values[entry] = value;
        tails[listId] = entry;
        count[listId]++;
        if(oldTail == -1) {
            heads[listId] = entry;
        } else {
            nexts[oldTail] = entry;
        }

        nexts[entry] = -1;
        prevs[entry] = oldTail;
        size++;
        return entry;
    }

    /** The number of values in all lists. */
    public int size() {
        return size;
    }

    /** The number of values in the given list. */
    public int size(final int listId) {
        if(listId >= count.length || listId < 0) {
            return 0;
        }
        return count[listId];
    }

    public ${type.javaType} removeFirst(final int listId) {
        final int entry = validateListAndGetFirstEntry(listId, heads);
        heads[listId] = nexts[entry];
        count[listId]--;
        if(tails[listId] == entry) {
            tails[listId] = -1;
        }
        if(nexts[entry] != -1) {
            prevs[nexts[entry]] = -1;
        }

        final ${type.javaType} value = ${type.cast}values[entry];
        values[entry] = ${type.name}Type.DEFAULT;;

        size--;
        addToFreeList(entry);

        return value;
    }

    public ${type.javaType} removeLast(final int listId) {
        final int entry = validateListAndGetFirstEntry(listId, tails);
        tails[listId] = prevs[entry];
        count[listId]--;
        if(heads[listId] == entry) {
            heads[listId] = -1;
        }
        if(prevs[entry] != -1) {
            nexts[prevs[entry]] = -1;
            prevs[entry] = -1;
        }

        final ${type.javaType} value = ${type.cast}values[entry];
        values[entry] = ${type.name}Type.DEFAULT;;

        size--;
        addToFreeList(entry);

        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        IntArray.fill(heads, -1);
        IntArray.fill(tails, -1);
        IntArray.fill(nexts, -1);
        IntArray.fill(prevs, -1);
        IntArray.fill(count, 0);
        ${type.name}Array.fill(values, ${type.name}Type.DEFAULT);
        freeList = -1;
        size = 0;
    }

    public ${type.name}Iterator${generics} iterator(final int listId) {
        return iterator(listId, null);
    }

    public ${type.name}Iterator${generics} iterator(final int listId, final ${type.name}Iterator${generics} iterator) {
        final ${type.name}Iterator${generics} localIterator = iterator != null ? iterator : new ForwardIterator${generics}();
        ((ForwardIterator${generics})localIterator).reset(listId);
        return localIterator;
    }

    public ${type.name}Iterator${generics} reverseIterator(final int listId) {
        return reverseIterator(listId, null);
    }

    public ${type.name}Iterator${generics} reverseIterator(final int listId, final ${type.name}Iterator${generics} iterator) {
        final ${type.name}Iterator${generics} localIterator = iterator != null ? iterator : new ReverseIterator${generics}();
        ((ReverseIterator${generics})localIterator).reset(listId);
        return localIterator;
    }

    private int validateListAndGetFirstEntry(final int listId, final int[] start) {
        if(size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        if(listId < 0 || listId >= heads.length) {
            throw new NoSuchElementException("Invalid List: " + listId);
        }
        final int entry = start[listId];
        if(entry < 0) {
            throw new NoSuchElementException(String.format("List %d is empty (entry=%d)", listId, entry));
        }
        return entry;
    }

    private class ForwardIterator${generics} implements ${type.name}Iterator${generics} {
        private int entry;
        private void reset(final int listId) {
            this.entry = listId >= heads.length ? -1 : heads[listId];
        }
        @Override
        public ${type.javaType} next() {
            if(entry < 0) {
                throw invalidEntry(entry);
            }
            final var value = values[entry];
            entry = nexts[entry];
            return ${type.cast}value;
        }

        @Override
        public boolean hasNext() {
            return entry >= 0;
        }
    }

    private class ReverseIterator${generics} implements ${type.name}Iterator${generics} {
        private int entry;
        private void reset(final int listId) {
            this.entry = listId >= tails.length ? -1 : tails[listId];
        }
        @Override
        public ${type.javaType} next() {
            if(entry < 0) {
                throw invalidEntry(entry);
            }
            final var value = values[entry];
            entry = prevs[entry];
            return ${type.cast}value;
        }

        @Override
        public boolean hasNext() {
            return entry >= 0;
        }
    }

}