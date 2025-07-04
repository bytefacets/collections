<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.heap;

import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.exception.InvalidEntryException;
import com.bytefacets.collections.exception.RangeCheckException;
import com.bytefacets.collections.types.${type.name}Type;
import com.bytefacets.collections.types.IntType;

/** Base class for a heap of ${type.javaType}. */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public abstract class Base${type.name}Heap${generics} extends BaseHeap {
    private ${type.arrayType}[] keys;
    private ${type.name}Type.Cmp comp = ${type.name}Type.Asc;
    private ${type.arrayType} defaultKey = ${type.name}Type.DEFAULT;

    protected Base${type.name}Heap(final int initialCapacity) {
        super(initialCapacity);
        keys = ${type.name}Array.create(initialCapacity, defaultKey);
    }

    /** The comparator in use for the heap. */
    public ${type.name}Type.Cmp getComparator() {
        return comp;
    }

    /**
     * Resets the comparator and rebuilds the heap.
     *
     * @throws NullPointerException if the given comparator is null
     */
    public void setComparator(final ${type.name}Type.Cmp newComparator) {
        if(newComparator == null) {
            throw new NullPointerException("Cannot assign null Comparator");
        }

        if(comp == newComparator) {
            return;
        }

        comp = newComparator;

        for(int i = 0; i < size; i++) {
            final int entry = tree[i];
            filterUp(i, keys[entry], entry);
        }
        modCount++;
    }

    /** The current default key */
    public ${type.javaType} getDefaultKey() {
        return ${type.cast}defaultKey;
    }

    /**
     * Sets a default key on the heap, but can only be called when the heap is empty.
     *
     * @throws UnsupportedOperationException if the heap has anything in it already
     */
    public void setDefaultKey(final ${type.javaType} defaultKey) {
        if(size > 0) {
            throw new UnsupportedOperationException("SetDefaultKey("+defaultKey+"): collection is not empty ("+getSize()+")" );
        }
        if(!${type.name}Type.EqImpl.areEqual(this.defaultKey, defaultKey)) {
            this.defaultKey = defaultKey;
            ${type.name}Array.fill(keys, defaultKey, size, keys.length);
        }
    }

    /** Clears the contents of the heap. */
    public void clear() {
        IntArray.fill(tree, IntType.DEFAULT);
        IntArray.fill(inverse, IntType.DEFAULT);
        ${type.name}Array.fill(keys, defaultKey, 0, size);
        clearValues();
        freeCount = 0;
        size = 0;
        modCount++;
    }

    /**
     * Inserts the key into the heap, returning the value's entry.
     * The entry is a stable reference to the value.
     */
    public int insert(final ${type.javaType} key) {
        // insert the new node as the bottom-rightmost leaf
        final int child = size;
        final int entry = allocateEntry(key);
        filterUp(child, key, entry); // place in the heap
        return entry;
    }

    /**
     * Removes the value at the given entry.
     *
     * @throws InvalidEntryException if the entry was not active in the heap
     * @throws RangeCheckException if the entry was outside the bounds of the underlying array
     */
    public void removeAt(final int entry) {
        RangeCheckException.assertWithinRange(0, inverse.length - 1, entry, "entry");

        final int node = inverse[entry];

        // the node is unknown or somehow outside the limits of the heap
        if(node == -1 || node >= size) {
            throw new InvalidEntryException(
                String.format("Tried to remove entry that does not exist: entry=%d, node=%d, size=%d",
                              size, entry, node, size));
        }

        keys[entry] = defaultKey;
        clearValueAt(entry);
        size--;

        // the last leaf in the values
        final int lastEntry = tree[size];
        final ${type.javaType} key = ${type.cast}keys[lastEntry];

        // put the entry on the free list
        freeCount++;
        tree[size] = entry;
        inverse[entry] = size;

        // shortcut if we this node was the last one
        if(node == size) {
            return;
        }

        filterDown(filterUp(node, key, lastEntry), key, lastEntry);
    }

    /**
     * Resets the key for the given entry. This can be useful for timers, which may repeat:
     * the repeat can be accomplished by setting a new time, but the entry will remain stable.
     */
    public void resetKey(final int entry, final ${type.javaType} newKey) {
        keys[entry] = newKey;
        filterDown(filterUp(inverse[entry], newKey, entry), newKey, entry);
    }

    /**
     * Returns the key at the given entry. Note this is not range-checked.
     *
     * @throws IndexOutOfBoundsException if entry is outside the bounds of the underlying array
     */
    public ${type.javaType} getKeyAt(final int entry) {
        return ${type.cast}keys[entry];
    }

    /** Returns true if the key is present in the heap. */
    public boolean containsKey(final ${type.javaType} key) {
        return getFirstEntry(key) != -1;
    }

    public int getFirstEntry(final ${type.javaType} key) {
        for(int i = 0; i < size; i++) {
            final int entry = tree[i];
            if(comp.compare(key, keys[entry]) == 0) {
                return entry;
            }
        }
        return -1;
    }

    protected int allocateEntry(final ${type.javaType} key) {
        int t;
        if(freeCount > 0) {
            t = tree[size];
            freeCount--;
        } else {
            final int oldLen = keys.length;
            if(size >= oldLen) {
                final int newCapacity = calculateNewCapacity();
                growHeap(newCapacity);
                growKeys(newCapacity);
                growValues(newCapacity);
            }
            t = size;
        }
        keys[t] = key;

        modCount++;
        size++;

        return t;
    }

    protected int filterUp(int child, final ${type.arrayType} key, final int entry) {
        while(child != 0) {
            // see if the child needs to move up by comparing to the parent
            final int parent = (child - 1) >> 1;
            final ${type.arrayType} parentKey = keys[tree[parent]];
            if(comp.compare(key, parentKey) >= 0) {
                break; // we're ok here
            }

            tree[child] = tree[parent];
            inverse[tree[child]] = child;
            child = parent;
        }

        tree[child] = entry;
        inverse[entry] = child;

        return child;
    }

    protected void filterDown(int parent, final ${type.arrayType} key, final int entry) {
        int child;
        // while parent is not a leaf node...
        while((child = (parent << 1) + 1) < size) {
            ${type.arrayType} childKey = keys[tree[child]];

            // if there's a right child, compare it to the left
            if(child + 1 < size) {
                final ${type.arrayType} rightKey = keys[tree[child + 1]];
                if(comp.compare(childKey, rightKey) > 0) {
                    // the right key comes before the left key
                    child++;
                    childKey = rightKey;
                }
            }

            // compare our key to the earliest child key. We use < to preserve insert order.
            if(comp.compare(key, childKey) < 0) {
                break; // we're ok here
            }

            tree[parent] = tree[child];
            inverse[tree[parent]] = parent;
            parent = child;
        }

        tree[parent] = entry;
        inverse[entry] = parent;
    }

    protected void growKeys(final int capacity) {
        this.keys = ${type.name}Array.copyOf(keys, capacity, defaultKey);
    }

    protected abstract void growValues(int size);

    protected abstract void clearValues();

    protected abstract void clearValueAt(int entry);
}
