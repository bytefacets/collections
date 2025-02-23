<#ftl strip_whitespace=true>
package com.bytefacets.collections.store;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

public final class ${type.name}ChunkStore<#if type.isGeneric()><T></#if> implements ${type.name}Store<#if type.isGeneric()><T></#if> {
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

    <#if type.isGeneric()>@SuppressWarnings("unchecked")</#if>
    @Override
    public ${type.javaType} get${type.name}(final int index) {
        if(index >= capacity) {
            return <#if type.isGeneric()>(T)</#if>${type.name}Type.DEFAULT;
        }
        final int offset = index & chunkMask;
        final int chunk = index >> shift;
        return <#if type.isGeneric()>(T)</#if>chunks[chunk][offset];
    }

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
