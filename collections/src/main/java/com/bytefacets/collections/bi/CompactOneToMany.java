// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

import com.bytefacets.collections.functional.IntConsumer;
import com.bytefacets.collections.hash.LongIndexedSet;
import com.bytefacets.collections.types.Pack;

/**
 * A collection which can hold and access integer associations in 1-n relationships. It is called
 * "Compact" because the integers should be 0-based as the integer values are used as array offsets
 * internally for performance and memory reasons. If your domain values are not compact, they can be
 * made compact by first performing a range transformation using an {@link
 * com.bytefacets.collections.hash.IntIndexedSet IntIndexedSet}.
 */
public class CompactOneToMany {
    protected static final int UNSET = -1;
    public static final long DEFAULT = Pack.packToLong(-1, -1);
    private final LongIndexedSet mappings;
    protected final Side left;
    private final Mapping leftMapping;

    public CompactOneToMany() {
        this(16, 16, true);
    }

    @SuppressWarnings("this-escape")
    public CompactOneToMany(
            final int initialLefts, final int initialMappingCapacity, final boolean includeCounts) {
        this.mappings = new LongIndexedSet(initialMappingCapacity);
        this.left = new Side(initialLefts, initialMappingCapacity, includeCounts);
        this.leftMapping = new Mapping(left, this::getRightAt);
    }

    /** The number of associations in the collection. */
    public int size() {
        return mappings.size();
    }

    /**
     * Registers an association between leftKey and rightKey, returning the stable entry of the
     * association. If the association is already present, the existing entry is returned.
     */
    public int put(final int leftKey, final int rightKey) {
        final long key = Pack.packToLong(leftKey, rightKey);
        final int before = mappings.size();
        final int entry = mappings.add(key);
        final int after = mappings.size();

        if (before == after) { // already know this
            return entry;
        }

        insertLeft(leftKey, entry);
        insertRight(rightKey, entry);
        return entry;
    }

    /**
     * Removes the association between leftKey and rightKey if it exists and returns the entry that
     * held the association. If the association doesn't exist, it's a no-op and -1 is returned.
     */
    public int remove(final int leftKey, final int rightKey) {
        final int entry = mappings.lookupEntry(Pack.packToLong(leftKey, rightKey));
        if (entry == -1) {
            return -1;
        }
        removeLeft(leftKey, entry);
        removeRight(rightKey, entry);
        mappings.removeAt(entry);
        return entry;
    }

    /**
     * Removes the association at the given entry.
     *
     * @throws IndexOutOfBoundsException if entry is out of bounds on the underlying association
     *     array
     */
    public int removeAt(final int entry) {
        final long key = mappings.getKeyAt(entry);
        removeLeft(Pack.unpackHiInt(key), entry);
        removeRight(Pack.unpackLoInt(key), entry);
        mappings.removeAt(entry);
        return entry;
    }

    /** Whether the collection contains the association between leftKey and rightKey. */
    public boolean contains(final int leftKey, final int rightKey) {
        return mappings.containsKey(Pack.packToLong(leftKey, rightKey));
    }

    /**
     * Returns the entry for the association between leftKey and rightKey if it exists, or -1 if it
     * doesn't exist.
     */
    public int lookupEntry(final int leftKey, final int rightKey) {
        return mappings.lookupEntry(Pack.packToLong(leftKey, rightKey));
    }

    /**
     * Fluent-style method returning an object to access associations for the given leftKey. The
     * Mapping that is returned is singular for interacting with lefts in this collection. In other
     * words, the Mapping returned is reused for every call of withLeft on this collection.
     */
    public Mapping withLeft(final int leftKey) {
        leftMapping.resetKey(leftKey);
        return leftMapping;
    }

    /**
     * Returns the leftKey for the given association entry.
     *
     * @throws IndexOutOfBoundsException if the entry is out of bounds
     */
    public int getLeftAt(final int entry) {
        final long association = mappings.getKeyAt(entry);
        return Pack.unpackHiInt(association);
    }

    /**
     * Returns the rightKey for the given association entry.
     *
     * @throws IndexOutOfBoundsException if the entry is out of bounds
     */
    public int getRightAt(final int entry) {
        final long association = mappings.getKeyAt(entry);
        return Pack.unpackLoInt(association);
    }

    /**
     * Removes the entry from the collection, but leaves the key and value in place. This can be
     * useful in compound operations where you might process the key again, or just encounter a new
     * key, but don't want to re-allocate the entry. Once you're ready to allow the reallocation of
     * the entry, use the freeReservedEntry method. If you don't free the reserved entry later, the
     * set will never use the entry again, and result in a memory leek.
     *
     * @throws IndexOutOfBoundsException if the entry is out of bounds
     */
    public void removeAtAndReserve(final int entry) {
        final long key = mappings.getKeyAt(entry);
        mappings.removeAtAndReserve(entry);
        removeLeft(Pack.unpackHiInt(key), entry);
        removeRight(Pack.unpackLoInt(key), entry);
    }

    /**
     * Used in combination with the removeAtAndReserve method, this clears the key and value at the
     * reserved entry and puts the entry back on the free list. This does not check whether you
     * first reserved the entry. Calling this with active entries can corrupt the collection.
     *
     * @throws IndexOutOfBoundsException if the entry is out of bounds
     */
    public void freeReservedEntry(final int entry) {
        mappings.freeReservedEntry(entry);
    }

    protected void insertLeft(final int leftKey, final int entry) {
        left.insertEntry(leftKey, entry);
    }

    protected void insertRight(final int rightKey, final int entry) {}

    protected void removeLeft(final int leftKey, final int entry) {
        left.removeEntry(leftKey, entry);
    }

    protected void removeRight(final int rightKey, final int entry) {}

    /** Iterates each entry */
    public void forEachEntry(final IntConsumer consumer) {
        mappings.forEachEntry(consumer);
    }

    protected interface EntryLookupFunc {
        int getAt(int entry);
    }
}
