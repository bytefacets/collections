<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.heap;

import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.arrays.${key.name}Array;
import com.bytefacets.collections.arrays.${value.name}Array;
import com.bytefacets.collections.types.IntType;
import com.bytefacets.collections.types.${key.name}Type;
import com.bytefacets.collections.types.${value.name}Type;

/**
 * An indexed heap map, which allows for associating a value with an entry in a heap. This can
 * useful for things like timers where you might associate some callback (the value) with a
 * firing time (the key).
 */
<#if key.generic || value.generic>@SuppressWarnings("unchecked")</#if>
public class ${key.name}${value.name}Heap${generics} extends Base${key.name}Heap${key.declaration} {
    private ${value.arrayType} defaultValue = ${value.cast}${value.name}Type.DEFAULT;
    private ${value.arrayType}[] values;

    public ${key.name}${value.name}Heap(final int initialCapacity) {
        super(initialCapacity);
        values = ${value.name}Array.create(initialCapacity, defaultValue);
    }

    /** The current default value. */
    public ${value.javaType} getDefaultValue() {
        return ${value.cast}defaultValue;
    }

    /**
     * Sets a default value on the heap, but can only be called when the heap is empty.
     *
     * @throws UnsupportedOperationException if the heap has anything in it already
     */
    public void setDefaultValue(final ${value.javaType} defaultValue) {
        if(size > 0) {
            throw new RuntimeException("SetDefaultValue("+defaultValue+"): collection is not empty ("+getSize()+")" );
        }
        if(!${value.name}Type.EqImpl.areEqual(this.defaultValue, defaultValue)) {
            this.defaultValue = defaultValue;
            ${value.name}Array.fill(values, defaultValue, size, values.length);
        }
    }

    /** Inserts the key into the heap and associates the value with the key's entry. */
    public int insert(final ${key.javaType} key, final ${value.javaType} value) {
        final int entry = insert(key);
        values[entry] = value;
        return entry;
    }

    /**
     * Returns the value at the given entry. Note this is not range-checked.
     *
     * @throws IndexOutOfBoundsException if entry is outside the bounds of the underlying array
     */
    public ${value.javaType} getValueAt(final int entry) {
        return ${value.cast}values[entry];
    }

    /**
     * Returns the value at the entry, or a default value if the entry is out of range. Note that
     * this method does not validate whether the entry is active within the heap.
     */
    public ${value.javaType} getValueAtOrDefault(final int entry, final ${value.javaType} defaultValue) {
        if(entry < 0 || entry >= values.length) {
            return defaultValue;
        }
        return ${value.cast}values[entry];
    }

    /**
     * Sets the value at the given entry. Note this is not range-checked.
     *
     * @throws IndexOutOfBoundsException if entry is outside the bounds of the underlying array
     */
    public void setValueAt(final int entry, final ${value.javaType} value) {
        values[entry] = value;
    }

    protected void growValues(final int size) {
        values = ${value.name}Array.copyOf(values, size, defaultValue);
    }

    protected void clearValueAt(final int entry) {
        values[entry] = defaultValue;
    }

    @Override
    protected void clearValues() {
        ${value.name}Array.fill(values, defaultValue, 0, size);
    }
}
