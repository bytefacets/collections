<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

/**
 * A store for ${type.javaType} values which are stored in array "chunks". Using chunks is
 * especially beneficial for resizing because only new chunks need to be allocated and the
 * copying is at the chunk level.
 */
public final class ${type.name}ChunkStore${generics} implements ${type.name}Store${generics} {
    private final int chunkSize;
    private final int chunkMask;
    private final int shift;
    private ${type.arrayType}[][] chunks;
    private int capacity;
    private int limit;

    public ${type.name}ChunkStore(final int initialSize, final int chunkSize) {
        this.chunkSize = NumUtils.nextPowerOf2(chunkSize);
        this.chunkMask = chunkSize - 1;
        this.shift = Long.numberOfTrailingZeros(this.chunkSize);
        final int requiredChunks = (int) Math.ceil((double)initialSize / (double)this.chunkSize);
        this.chunks = new ${type.arrayType}[requiredChunks][this.chunkSize];
        this.capacity = chunks.length * this.chunkSize;
    }

    /** Returns the value at the given index. */
    <#if type.generic>@SuppressWarnings("unchecked")</#if>
    @Override
    public ${type.javaType} get${type.name}(final int index) {
        if(index >= capacity) {
            return <#if type.generic>(T)</#if>${type.name}Type.DEFAULT;
        }
        final int offset = index & chunkMask;
        final int chunk = index >> shift;
        return <#if type.generic>(T)</#if>chunks[chunk][offset];
    }

    /**
     * Sets the value at the given index, and grows the store to accommodate the index
     * if necessary.
     */
    @Override
    public void set${type.name}(final int index, final ${type.javaType} value) {
        if(index >= capacity) {
            grow(index);
        }
        limit = Math.max(limit, index);
        final int offset = index & chunkMask;
        final int chunk = index >> shift;
        chunks[chunk][offset] = value;
    }

    /** The current capacity of this store. */
    public int getCapacity() {
        return capacity;
    }

    // VisibleForTesting
    int numChunks() {
        return chunks.length;
    }

    private void grow(final int index) {
        final int requiredChunks = (int) Math.ceil((double)index / (double)this.chunkSize);
        final int oldLen = chunks.length;
        this.chunks = Arrays.copyOf(chunks, requiredChunks);
        for(int i = oldLen; i < chunks.length; i++) {
            chunks[i] = ${type.name}Array.create(chunkSize);
        }
        this.capacity = chunks.length * this.chunkSize;
    }
}
