package com.bytefacets.collections;

import static com.bytefacets.collections.exception.ResizeException.cannotResize;

public final class CapacityCalculator {
    static final int MAX_CAPACITY = Integer.MAX_VALUE / 4 + 1; // must be a power of 2

    private CapacityCalculator() {}

    public static int calculateNewCapacity(
            final Object owner, final int capacity, final int capacityThreshold) {
        if (capacity == MAX_CAPACITY) {
            if (capacityThreshold == MAX_CAPACITY) {
                throw cannotResize(owner.getClass(), "int[]", capacity, capacity + 1);
            }
            return capacity; // we've done all we can
        }

        // come up with a new capacity according to the policy
        final int newCapacity = capacity << 1; // e.g. double

        if (newCapacity <= capacity) { // less-than in case of overflow
            throw cannotResize(owner.getClass(), "int[]", capacity, capacity + 1);
        }

        // limit capacity to a MAX of MAX_CAPACITY
        return Math.min(newCapacity, MAX_CAPACITY);
    }
}
