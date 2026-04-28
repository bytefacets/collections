<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

import com.bytefacets.collections.exception.InitializationException;
import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.types.${type.name}Type;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;

/**
 * A store for ${type.javaType} values which are stored in MemorySegment "chunks". Using chunks is
 * especially beneficial for resizing because only new chunks need to be allocated.
 */
public final class Large${type.name}ChunkStore${generics} implements Large${type.name}Store${generics} {
    private final Arena arena;
    private final int chunkSize;
    private final int chunkMask;
    private final int shift;
    private final List<MemorySegment> chunks;
    private long capacity;
    private long limit;
    private boolean closed;

    public Large${type.name}ChunkStore(final long initialSize, final int chunkSize) {
        InitializationException.assertMinimum(1, initialSize, "initialSize");
        InitializationException.assertMinimum(2, chunkSize, "chunkSize");

        this.arena = Arena.ofConfined();
        this.chunkSize = NumUtils.nextPowerOf2(chunkSize);
        this.chunkMask = chunkSize - 1;
        this.shift = Long.numberOfTrailingZeros(this.chunkSize);
        final int requiredChunks = (int) Math.ceil((double)initialSize / (double)this.chunkSize);
        this.chunks = new ArrayList<>(requiredChunks);
        grow(initialSize);
    }

    /** Releases allocated memory. */
    @Override
    public void close() {
        if(closed) {
            throw new IllegalStateException("Store is closed.");
        }
        closed = true;
        capacity = -1;
        limit = -1;
        chunks.clear();
        arena.close();
    }

    /** Returns the value at the given index. */
    <#if type.generic>@SuppressWarnings("unchecked")</#if>
    @Override
    public ${type.javaType} get${type.name}(final long index) {
        assertNotClosed();
        if(index >= capacity) {
            return <#if type.generic>(T)</#if>${type.name}Type.DEFAULT;
        }
        final int offset = (int)(index & chunkMask);
        final int chunk = (int)(index >>> shift);
        final MemorySegment segment = chunks.get(chunk);
        return segment.getAtIndex(${type.name}Type.VALUE_LAYOUT, offset);
    }

    /**
     * Sets the value at the given index, and grows the store to accommodate the index
     * if necessary.
     */
    @Override
    public void set${type.name}(final long index, final ${type.javaType} value) {
        assertNotClosed();
        if(index >= capacity) {
            grow(index+1);
        }
        limit = Math.max(limit, index);
        final int offset = (int)(index & chunkMask);
        final int chunk = (int)(index >> shift);
        final MemorySegment segment = chunks.get(chunk);
        segment.setAtIndex(${type.name}Type.VALUE_LAYOUT, offset, value);
    }

    /** The current capacity of this store. */
    public long getCapacity() {
        return capacity;
    }

    // VisibleForTesting
    int numChunks() {
        return chunks.size();
    }

    private void grow(final long index) {
        final int requiredChunks = (int) Math.ceil((double)index / (double)this.chunkSize);
        final int oldLen = chunks.size();
        for(int i = oldLen; i < requiredChunks; i++) {
            chunks.add(arena.allocate(${type.name}Type.VALUE_LAYOUT, chunkSize));
        }
        this.capacity = chunks.size() * ((long)this.chunkSize);
    }

    private void assertNotClosed() {
        if(closed) throw new IllegalStateException("Store is closed");
    }
}
