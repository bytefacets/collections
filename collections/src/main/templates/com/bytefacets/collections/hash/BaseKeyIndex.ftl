<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.hash;

import com.bytefacets.collections.${type.name}IndexedCollection;
import com.bytefacets.collections.arrays.*;
import com.bytefacets.collections.functional.${type.name}Consumer;
import com.bytefacets.collections.types.*;

/**
 * Base class for an Indexed collection
 */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public abstract class Base${type.name}Index${generics} extends BaseHash implements ${type.name}IndexedCollection${generics} {
    private ${type.name}Type.Hash hashFunc  = ${type.name}Type.HashImpl;
    private ${type.name}Type.Eq   equalFunc = ${type.name}Type.EqImpl;
    private ${type.arrayType}[] keys;

    protected Base${type.name}Index(final int initialCapacity, final double loadFactor) {
        super(initialCapacity, loadFactor);
        keys = ${type.name}Array.create(capacity);
    }

    protected void copyFrom(final Base${type.name}Index${generics} source) {
        super.copyFrom(source);
        
        // keys array should be big enough
        if(keys.length < source.keys.length ) {
            keys = ${type.name}Array.create(source.keys.length);
        } else {
            ${type.name}Array.fill(keys, ${type.name}Type.DEFAULT, source.keys.length, keys.length - source.keys.length);
        }
        
        System.arraycopy(source.keys, 0, keys, 0, source.keys.length);

        capacity = keys.length;
        capThreshold = (int) (capacity * loadFactor);
    }

    @Override
    public void forEach(final ${type.name}Consumer${generics} consumer) {
        if (size == 0) {
            return;
        }
        int hits = 0;
        final int expectedHits = size;
        for (int head = 0; head < heads.length && hits < expectedHits; head++) {
            for (int e = heads[head]; e >= 0; ) {
                final int next = nexts[e]; // pull out next in case the function removes the entry
                hits++;
                consumer.accept(${type.cast}keys[e]);
                e = next;
            }
        }
    }

    @Override
    public int lookupEntry(final ${type.javaType} key) {
        final int head = computeHead( key );
        for(int e = heads[head]; e >= 0; e = nexts[e]) {
            if(equalFunc.areEqual(key, keys[e])) {
                return e;
            }
        }
        return -1;
    }

    /**
     * Returns the key at the given entry.
     *
     * @throws IndexOutOfBounds if the entry is outside the bounds of the underlying array
     */
    @Override
    public ${type.javaType} getKeyAt(final int entry) {
        return ${type.cast}keys[entry];
    }

    /** Whether the key exists in the collection. */
    @Override
    public boolean containsKey(final ${type.javaType} key) {
        return lookupEntry(key) != -1;
    }

    /**
     * Removes the key from the collection, returning the entry that was freed, or -1 if the key
     * was not found.
     */
    @Override
    public int remove(final ${type.javaType} key) {
        return remove(key, true);
    }

    /**
     * Removes the key at the given entry.
     */
    @Override
    public void removeAt(final int entry) {
        final int head = getHead(entry);
        int prev = -1;
        for(int e = heads[head]; e >= 0; prev = e, e = nexts[e]) {
            if(e != entry) {
                continue;
            }

            final int next = nexts[e];
            if(prev < 0) {
                heads[head] = next; // new head
            } else {
                nexts[prev] = next;
            }

            freeReservedEntry(e);
            size--;
            modificationCount++;

            break;
        }
    }

    /**
     * Removes the entry from the hash table, but leaves the key and value in place. This can be
     * useful in compound operations where you might process the key again, or just encounter a new
     * key, but don't want to re-allocate the entry. Once you're ready to allow the reallocation
     * of the entry, use the freeReservedEntry method. If you don't free the reserved entry later,
     * the set will never use the entry again, and result in a memory leek.
     */
    public void removeAtAndReserve(final int entry) {
        remove(${type.cast}keys[entry], false);
    }

    /**
     * Used in combination with the removeAtAndReserve method, this clears the key and value
     * at the reserved entry and puts the entry back on the free list. This does not check
     * whether or not you first reserved the entry. Calling this with active entries can corrupt
     * the collection.
     */
    public void freeReservedEntry(final int entry) {
        nexts[entry] = freeList;
        freeList = entry;

        // reset key and value
        keys[entry] = ${type.name}Type.DEFAULT;
        removeValue(entry);
    }

    protected int remove(final ${type.javaType} key, final boolean free) {
        final int head = computeHead(key);
        final int headEntry = heads[head];
        if(headEntry < 0) {
            return -1;
        }
        int prev = -1;
        for(int e = headEntry; e >= 0; prev = e, e = nexts[e]) {
            if(!equalFunc.areEqual(key, keys[e])) {
                continue;
            }

            int next = nexts[e];
            if(prev < 0)  {
                heads[head] = next; // new head
            } else {
                nexts[prev] = next;
            }

            if(free) {
                freeReservedEntry(e);
            } else {
                nexts[e] = -1;
                // leave key and value set
            }
            size--;
            modificationCount++;

            return e;
        }
        return -1;
    }

    /**
     * Adds the key to the collection if it's not yet in the collection and returns the
     * stable entry assigned to the key.
     */
    public int add(final ${type.javaType} key) {
        int head = computeHead(key);
        for(int e = heads[head]; e >= 0; e = nexts[e]) {
            if(equalFunc.areEqual(key, keys[e])) {
                return e;
            }
        }

        // find a new entry for this key
        final int entry = allocateNewEntry();
        if(entry >= capThreshold) {
            rehash();
            head = computeHead(key);
        }
        // link it in the table
        registerNewEntryForHead(head, entry);

        // assign key to the new entry
        keys[entry] = key;

        // some light record-keeping
        updateMaxHead(head);
        size++;
        modificationCount++;

        return entry;
    }

    private int allocateNewEntry() {
        final int entry;
        if(freeList != -1) {  // use free list if present
            entry = freeList;
            freeList = nexts[entry];
        } else {
            entry = nextUnusedEntry;
            nextUnusedEntry++;
        }
        return entry;
    }

    private void registerNewEntryForHead(final int head, final int entry) {
        // and link the entry in the hash table
        if(heads[head] >= 0) {
            // if there's a head, point this entry to the head
            nexts[entry] = heads[head];
        } else {
            // store reference to the head in negative space
            nexts[entry] = -head - 1;
        }
        heads[head] = entry;
    }

    private int getHead(int entry) {
        for(; entry >= 0; entry = nexts[entry]) {
        }
        return -entry - 1; 
    }

    protected int computeHead(final ${type.javaType} key) {
        final int hash = hashFunc.code(key) & Integer.MAX_VALUE;
        return hash & (heads.length - 1);
    }

    private void rehash() {
        // prepare swap
        final int[] oldHeads = heads;
        final int[] oldNexts = nexts;
        final ${type.arrayType}[] oldKeys  = keys;

        calculateNewCapacity();

        heads = IntArray.create(capacity, -1);
        nexts = IntArray.create(capacity, -1);

        // rebuild
        growKeys(capacity);
        growValues(capacity);

        // walk through the old heads and process each list
        final int len = Math.min(maxHead + 1, oldHeads.length); // limit loop
        maxHead = 0;
        for(int i = 0; i < len; i++) {
            int entry = oldHeads[i];
            while(entry >= 0) {
                final int newHead = computeHead(${type.cast}oldKeys[entry]);
                registerNewEntryForHead(newHead, entry);
                updateMaxHead(newHead);
                entry = oldNexts[entry]; // the next entry in the old list
            }
        }
        modificationCount++;
    }

    @Override
    protected void growKeys(final int size) {
        keys = ${type.name}Array.ensureSize(keys, size);
    }

    @Override
    protected void clearKeys() {
        ${type.name}Array.fill(keys, ${type.name}Type.DEFAULT, 0, Math.min(nextUnusedEntry, keys.length));
    }
    
    protected void setEntryKey(final int entry, final ${type.javaType} key) {
        keys[entry] = key;
    }
}
