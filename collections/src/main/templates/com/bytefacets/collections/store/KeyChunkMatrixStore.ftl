<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.exception.InitializationException;
import com.bytefacets.collections.exception.RangeCheckException;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

/**
 * A matrix store for ${type.javaType} values which are stored in array "chunks". Using chunks is
 * especially beneficial for resizing because only new chunks need to be allocated and the
 * copying is at the chunk level.
 */
public final class ${type.name}ChunkMatrixStore${generics} implements ${type.name}MatrixStore${generics} {
    private final int chunkSize;
    private final int chunkMask;
    private final int shift;
    private final int numFields;
    private ${type.arrayType}[][] chunks;
    /** The current full row capacity of the store. */
    private int capacity;

    /**
     * @param initialSize the initial size of the store in terms of the number of groups of fields;
                          though depending on the chunkSize, you may see your initialSize larger
                          than requested; minimum valid value is 1
     * @param chunkSize   the size of the arrays used internally which impact data locality and growth.
     *                    minimum value is 2, and given values will be rounded up to the next power of 2
     * @param numFields   the number of logical fields in the store; minimum valid value is 1
     */
    public ${type.name}ChunkMatrixStore(final int initialSize, final int chunkSize, final int numFields) {
        assertValidConfig(initialSize, chunkSize, numFields);
        this.chunkSize = NumUtils.nextPowerOf2(chunkSize);
        this.chunkMask = chunkSize - 1;
        this.shift = Long.numberOfTrailingZeros(this.chunkSize);
        this.numFields = numFields;
        final int rawSize = initialSize * numFields;
        final int requiredChunks = (int) Math.ceil((double)rawSize / (double)this.chunkSize);
        this.chunks = new ${type.arrayType}[requiredChunks][this.chunkSize];
        this.capacity = (chunks.length * this.chunkSize) / numFields;
    }

    private void assertValidConfig(final int initialSize, final int chunkSize, final int numFields) {
        InitializationException.assertMinimum(1, initialSize, "initialSize");
        InitializationException.assertMinimum(2, chunkSize, "chunkSize");
        InitializationException.assertMinimum(1, numFields, "numFields");
    }

    /**
     * Returns the value at the given row and field. If the row is beyond the capacity
     * of the store, it will return the ${type.javaType} default value. The field should be
     * within the numFields that the store was instantiated with.
     *
     * @throws IndexOutOfBoundsException if the row is negative or if the calculated array
     *              index is beyond the bounds.
     * @throws com.bytefacets.collections.exception.RangeCheckException if the field is not within [0, numFields]
     */
    <#if type.generic>@SuppressWarnings("unchecked")</#if>
    @Override
    public ${type.javaType} get${type.name}(final int row, final int field) {
        RangeCheckException.assertWithinRange(0, numFields - 1, field, "field");
        if(row >= capacity) {
            return ${type.cast}${type.name}Type.DEFAULT;
        }
        final int absoluteIx = (row * numFields) + field;
        final int offset = absoluteIx & chunkMask;
        final int chunk = absoluteIx >> shift;
        return ${type.cast}chunks[chunk][offset];
    }

    /**
     * Sets the value at the given row and field, growing the store if necessary.
     *
     * @throws com.bytefacets.collections.exception.RangeCheckException if the field is not within [0, numFields)
     */
    @Override
    public void set${type.name}(final int row, final int field, final ${type.javaType} value) {
        RangeCheckException.assertWithinRange(0, numFields - 1, field, "field");
        if(row >= capacity) {
            grow(row+1);
        }
        final int absoluteIx = (row * numFields) + field;
        final int offset = absoluteIx & chunkMask;
        final int chunk = absoluteIx >> shift;
        chunks[chunk][offset] = value;
    }

    /** The current full row capacity of the store. */
    public int getCapacity() {
        return capacity;
    }

    // VisibleForTesting
    int numChunks() {
        return chunks.length;
    }

    private void grow(final int row) {
        final int rawSize = row * numFields;
        final int requiredChunks = (int) Math.ceil((double)rawSize / (double)chunkSize);
        final int oldLen = chunks.length;
        this.chunks = Arrays.copyOf(chunks, requiredChunks);
        for(int i = oldLen; i < chunks.length; i++) {
            chunks[i] = ${type.name}Array.create(chunkSize);
        }
        this.capacity = (chunks.length * chunkSize) / numFields;
    }
}
