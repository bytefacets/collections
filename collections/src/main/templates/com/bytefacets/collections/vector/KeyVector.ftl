<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.vector;

import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.functional.${type.name}Consumer;
import com.bytefacets.collections.functional.${type.name}Iterable;


/**
 * A wrapper around an array that takes care of growing the underlying array.
 */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}Vector${generics} implements ${type.name}Iterable${generics} {
    private ${type.arrayType}[] values;
    private int size = 0;
    private int nextPos = 0;

    public ${type.name}Vector(final int initialCapacity) {
        if(initialCapacity < 1) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        values = ${type.name}Array.create(initialCapacity);
    }

    /**
     * Ensures that the instance can accommodate the given size. If a specific size is
     * known ahead of time, this method can avoid going through multiple resizings.
     */
    public void ensureSize(final int size) {
        values = ${type.name}Array.ensureSize(values, size);
    }

    /** Appends the value to the vector, resizing if necessary to accommodate. */
    public void append(final ${type.javaType} value) {
        values = ${type.name}Array.ensureEntry(values, nextPos);
        values[nextPos++] = value;
        size++;
    }

    /**
     * The value at the given position. Note this is not range-checked.
     *
     * @throws IndexOutOfBoundsException if the entry is outside the bounds of the internal array
     */
    public ${type.javaType} valueAt(final int index) {
        return ${type.cast}values[index];
    }

    /** Method to consume the contents of the vector. */
    @Override
    public void forEach(final ${type.name}Consumer${generics} consumer) {
        for(int i = 0; i < size; i++) {
            consumer.accept(${type.cast}values[i]);
        }
    }

    /** The current number of elements appended. This is a constant-time value. */
    public int size() {
        return size;
    }

    /** Whether the vector is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    <#if type.name == "Generic" || type.name == "String">
    /** Resets the vector positions, and nulls out the contents. */
    <#else>
    /** Resets the vector positions, but leaves the primitive contents in place. */
    </#if>
    public void clear() {
        <#if type.name == "Generic" || type.name == "String">
        ${type.name}Array.fill(values, null, 0, nextPos);
        </#if>
        nextPos = 0;
        size = 0;
    }
}