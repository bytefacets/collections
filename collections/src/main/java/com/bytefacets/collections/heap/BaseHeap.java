// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.heap;

import static com.bytefacets.collections.exception.ResizeException.cannotResize;

import com.bytefacets.collections.arrays.IntArray;
import java.util.function.IntConsumer;

abstract class BaseHeap {
    protected static final int MAX_CAPACITY = Integer.MAX_VALUE / 4 + 1; // must be a power of 2
    protected int[] tree;
    protected int[] inverse;
    protected int freeCount;
    protected int size;
    protected int modCount = 0;

    protected BaseHeap(final int initialCapacity) {
        tree = IntArray.create(initialCapacity);
        inverse = IntArray.create(initialCapacity);
    }

    public int peek() {
        if (size == 0) {
            throw new RuntimeException("Heap is empty");
        }
        return tree[0];
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return tree.length;
    }

    public void forEachEntry(final IntConsumer entryConsumer) {
        for (int entry = 0; entry < size; entry++) {
            if (isValidEntry(entry)) {
                entryConsumer.accept(entry);
            }
        }
    }

    public boolean isValidEntry(final int entry) {
        return (entry < inverse.length && inverse[entry] >= 0 && inverse[entry] < size);
    }

    protected void growHeap(final int capacity) {
        tree = IntArray.copyOf(tree, capacity);
        inverse = IntArray.copyOf(inverse, capacity);
    }

    protected int calculateNewCapacity() {
        final int capacity = tree.length;
        if (capacity == MAX_CAPACITY) {
            throw cannotResize(getClass(), "int[]", capacity, capacity + 1);
        }

        // come up with a new capacity according to the policy
        final int newCapacity = capacity << 1; // e.g. double

        if (newCapacity <= capacity) { // less-than in case of overflow
            throw cannotResize(getClass(), "int[]", capacity, capacity + 1);
        }

        // limit capacity to a MAX of MAX_CAPACITY
        return Math.min(newCapacity, MAX_CAPACITY);
    }

    // @VisibleForTesting
    int modCount() {
        return modCount;
    }
}
