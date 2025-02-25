<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

public final class ${type.name}ChunkMatrixStore${generics} implements ${type.name}MatrixStore${generics} {
    private final int chunkSize;
    private final int chunkMask;
    private final int shift;
    private final int numFields;
    private ${type.arrayType}[][] chunks;
    /** The current full row capacity of the store. */
    private int capacity;

    public ${type.name}ChunkMatrixStore(final int initialSize, final int chunkSize, final int numFields) {
        this.chunkSize = NumUtils.nextPowerOf2(chunkSize);
        this.chunkMask = chunkSize - 1;
        this.shift = Long.numberOfTrailingZeros(this.chunkSize);
        this.numFields = numFields;
        final int rawSize = initialSize * numFields;
        final int requiredChunks = (int) Math.ceil((double)rawSize / (double)this.chunkSize);
        this.chunks = new ${type.arrayType}[requiredChunks][this.chunkSize];
        this.capacity = (chunks.length * this.chunkSize) / numFields;
    }

    <#if type.generic>@SuppressWarnings("unchecked")</#if>
    @Override
    public ${type.javaType} get${type.name}(final int row, final int field) {
        if(row >= capacity) {
            return <#if type.generic>(T)</#if>${type.name}Type.DEFAULT;
        }
        final int absoluteIx = (row * numFields) + field;
        final int offset = absoluteIx & chunkMask;
        final int chunk = absoluteIx >> shift;
        return <#if type.generic>(T)</#if>chunks[chunk][offset];
    }

    @Override
    public void set${type.name}(final int row, final int field, final ${type.javaType} value) {
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
