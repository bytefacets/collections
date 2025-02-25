<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.hash;

import com.bytefacets.collections.arrays.*;
import com.bytefacets.collections.functional.${value.name}Consumer;
import com.bytefacets.collections.exception.KeyNotFoundException;
import com.bytefacets.collections.types.*;

/**
 * A hash map associating ${key.javaType} keys and ${value.javaType} values.
 */
<#if generics != "">@SuppressWarnings("unchecked")</#if>
public class ${key.name}${value.name}IndexedMap${generics} extends Base${key.name}Index${key.declaration}{
    private ${value.arrayType}[] values;
    
    public ${key.name}${value.name}IndexedMap(final int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public ${key.name}${value.name}IndexedMap(final int initialCapacity, final double loadFactor) {
        super(initialCapacity, loadFactor);
        values = ${value.name}Array.create(capacity);
    }

    /**
     * Adds a key to the map, sets its value to the value type's default,
     * and returns the stable entry for the key in the map.
     */
    @Override
    public int add(final ${key.javaType} key) {
        final int entry = super.add(key);
        values[entry] = ${value.name}Type.DEFAULT;
        return entry;
    }

    /** Copies the contents of another map into this map. */
    public void copyFrom(final ${key.name}${value.name}IndexedMap${generics} source) {
        super.copyFrom(source);

        if(values.length < source.values.length) {
            values = ${value.name}Array.create(source.values.length);
        } else {
            ${value.name}Array.fill(values, ${value.name}Type.DEFAULT, source.values.length, values.length);
        }
        System.arraycopy(source.values, 0, values, 0, source.values.length);
    }

    /**
     * Gets the value for a key, but will throw if the key is not in the map.
     *
     * @throws KeyNotFoundException if the key is not in the map
     */
    public ${value.javaType} get(final ${key.javaType} key) {
        final int entry = lookupEntry(key);
        if(entry == -1) {
            throw new KeyNotFoundException(key);
        }
        return ${value.cast}values[entry];
    }

    /**
     * Gets the value at a specific entry in the map. This works well if you're using the entry
     * instead of the key in your code.
     *
     * @throws IndexOutOfBoundsException if the entry is outside the bounds of the internal array
     */
    public ${value.javaType} getValueAt(final int entry) {
        return ${value.cast}values[entry];
    }

    /**
     * Gets the value at a specific entry in the map, but will give you your default value
     * if the entry happens to be out of bounds. This method does not validate that the entry
     * is in use.
     */
    public ${value.javaType} getValueAtOrDefault(final int entry, final ${value.javaType} defaultValue) {
        if(entry < 0 || entry >= values.length) {
            return defaultValue;
        }
        return ${value.cast}values[entry];
    }

    /**
     * Associates a key and value in the map and returns the stable entry of the key in the map.
     */
    public int put(final ${key.javaType} key, final ${value.javaType} value) {
        final int entry = add(key);
        values[entry] = value;
        return entry;
    }

    /**
     * Replaces the value at a specific entry in the map.
     */
    public void putValueAt(final int entry, final ${value.javaType} value) {
        values[entry] = value;
    }

    /**
     * Iterates the values of the map passing them into a consumer. There is no guaranteed order.
     */
    public void forEachValue(final ${value.name}Consumer${value.declaration} consumer) {
        if (size == 0) {
            return;
        }
        int hits = 0;
        final int expectedHits = size;
        for (int head = 0; head < heads.length && hits < expectedHits; head++) {
            for (int e = heads[head]; e >= 0; ) {
                final int next = nexts[e]; // pull out next in case the function removes the entry
                hits++;
                consumer.accept(${value.cast}values[e]);
                e = next;
            }
        }
    }

    /**
     * Tries to get a value using a key, but will not throw a KeyNotFoundException if the key
     * is not in the map. Instead, it will return the provided defaultValue.
     */
    public ${value.javaType} getOrDefault(final ${key.javaType} key, final ${value.javaType} defaultValue) {
        final int entry = lookupEntry(key);
        if(entry == -1) {
            return defaultValue;
        }
        return ${value.cast}values[entry];
    }

    @Override
    protected void removeValue(final int entry) {
        values[entry] = ${value.name}Type.DEFAULT;
    }

    @Override
    protected void growValues(final int size) {
        values = ${value.name}Array.ensureSize(values, size);
    }

    @Override
    protected void clearValues() {
        ${value.name}Array.fill(values, ${value.name}Type.DEFAULT, 0, Math.min(nextUnusedEntry, values.length));
    }
}