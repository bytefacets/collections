package com.bytefacets.collections.bi;

import com.bytefacets.collections.functional.IntConsumer;
import com.bytefacets.collections.hash.LongIndexedSet;
import com.bytefacets.collections.types.Pack;

public class CompactOneToMany {
    protected static final int UNSET = -1;
    public static final long DEFAULT = Pack.packToLong(-1, -1);
    private final LongIndexedSet mappings;
    protected final Side left;
    private final Mapping leftMapping;

    public CompactOneToMany() {
        this(16, 16, true);
    }

    public CompactOneToMany(
            final int initialLefts, final int initialMappingCapacity, final boolean includeCounts) {
        this.mappings = new LongIndexedSet(initialMappingCapacity);
        this.left = new Side(initialLefts, initialMappingCapacity, includeCounts);
        this.leftMapping = new Mapping(left, this::getRightAt);
    }

    public int size() {
        return mappings.size();
    }

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

    public int removeAt(final int entry) {
        final long key = mappings.getKeyAt(entry);
        removeLeft(Pack.unpackHiInt(key), entry);
        removeRight(Pack.unpackLoInt(key), entry);
        mappings.removeAt(entry);
        return entry;
    }

    public boolean contains(final int leftKey, final int rightKey) {
        return mappings.containsKey(Pack.packToLong(leftKey, rightKey));
    }

    public int lookupEntry(final int leftKey, final int rightKey) {
        return mappings.lookupEntry(Pack.packToLong(leftKey, rightKey));
    }

    public Mapping withLeft(final int leftKey) {
        leftMapping.resetKey(leftKey);
        return leftMapping;
    }

    public int getLeftCount(final int key) {
        return left.count(key);
    }

    public int getLeftAt(final int entry) {
        final long association = mappings.getKeyAt(entry);
        return Pack.unpackHiInt(association);
    }

    public int getRightAt(final int entry) {
        final long association = mappings.getKeyAt(entry);
        return Pack.unpackLoInt(association);
    }

    public void removeAtAndReserve(final int entry) {
        mappings.removeAtAndReserve(entry);
    }

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

    public void forEachEntry(final IntConsumer consumer) {
        mappings.forEachEntry(consumer);
    }

    protected interface EntryLookupFunc {
        int getAt(int entry);
    }
}
