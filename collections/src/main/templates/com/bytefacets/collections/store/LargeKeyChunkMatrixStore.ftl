<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.exception.InitializationException;
import com.bytefacets.collections.exception.RangeCheckException;
import com.bytefacets.collections.types.${type.name}Type;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A matrix store for ${type.javaType} values which are stored in array "chunks". Using chunks is
 * especially beneficial for resizing because only new chunks need to be allocated and the
 * copying is at the chunk level.
 */
public final class Large${type.name}ChunkMatrixStore${generics} implements Large${type.name}MatrixStore${generics} {
    private final Arena arena;
    private final int chunkSize;
    private final int chunkMask;
    private final int shift;
    private final int numFields;
    private final List<MemorySegment> chunks;
    /** The current full row capacity of the store. */
    private long capacity;
    private boolean closed;

    /**
     * @param initialSize the initial size of the store in terms of the number of groups of fields;
                          though depending on the chunkSize, you may see your initialSize larger
                          than requested; minimum valid value is 1
     * @param chunkSize   the size of the arrays used internally which impact data locality and growth.
     *                    minimum value is 2, and given values will be rounded up to the next power of 2
     * @param numFields   the number of logical fields in the store; minimum valid value is 1
     */
    public Large${type.name}ChunkMatrixStore(final long initialSize, final int chunkSize, final int numFields) {
        InitializationException.assertMinimum(1, initialSize, "initialSize");
        InitializationException.assertMinimum(2, chunkSize, "chunkSize");
        InitializationException.assertMinimum(1, numFields, "numFields");

        this.arena = Arena.ofConfined();
        this.chunkSize = NumUtils.nextPowerOf2(chunkSize);
        this.chunkMask = chunkSize - 1;
        this.shift = Long.numberOfTrailingZeros(this.chunkSize);
        this.numFields = numFields;
        final long rawSize = initialSize * numFields;
        final int requiredChunks = (int) Math.ceil((double)rawSize / (double)this.chunkSize);
        this.chunks = new ArrayList<>(requiredChunks);
        grow(initialSize);
    }

    /** Releases allocated memory. */
    @Override
    public void close() {
        if(closed) {
            throw new IllegalStateException("Store is already closed.");
        }
        closed = true;
        arena.close();
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
    public ${type.javaType} get${type.name}(final long row, final int field) {
        assertNotClosed();
        RangeCheckException.assertWithinRange(0, numFields - 1, field, "field");
        if(row >= capacity) {
            return ${type.cast}${type.name}Type.DEFAULT;
        }
        final long absoluteIx = (row * numFields) + field;
        final int offset = (int)(absoluteIx & chunkMask);
        final int chunk = (int)(absoluteIx >> shift);
        final MemorySegment segment = chunks.get(chunk);
        return segment.getAtIndex(${type.name}Type.VALUE_LAYOUT, offset);
    }

    /**
     * Sets the value at the given row and field, growing the store if necessary.
     *
     * @throws com.bytefacets.collections.exception.RangeCheckException if the field is not within [0, numFields)
     */
    @Override
    public void set${type.name}(final long row, final int field, final ${type.javaType} value) {
        assertNotClosed();
        RangeCheckException.assertWithinRange(0, numFields - 1, field, "field");
        if(row >= capacity) {
            grow(row+1);
        }
        final long absoluteIx = (row * numFields) + field;
        final int offset = (int)(absoluteIx & chunkMask);
        final int chunk = (int)(absoluteIx >>> shift);
        final MemorySegment segment = chunks.get(chunk);
        segment.setAtIndex(${type.name}Type.VALUE_LAYOUT, offset, value);
    }

    /** The current full row capacity of the store. */
    public long getCapacity() {
        return capacity;
    }

    // VisibleForTesting
    int numChunks() {
        return chunks.size();
    }

    private void grow(final long row) {
        final long rawSize = row * numFields;
        final int requiredChunks = (int) Math.ceil((double)rawSize / (double)chunkSize);
        final int oldLen = chunks.size();
        for(int i = oldLen; i < requiredChunks; i++) {
            chunks.add(arena.allocate(${type.name}Type.VALUE_LAYOUT, chunkSize));
        }
        this.capacity = (chunks.size() * ((long)chunkSize)) / numFields;
    }

    private void assertNotClosed() {
        if(closed) throw new IllegalStateException("Store is closed");
    }
}
