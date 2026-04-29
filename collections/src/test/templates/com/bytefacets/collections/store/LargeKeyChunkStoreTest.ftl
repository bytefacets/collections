<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
<#assign typeClass = "${type.name}Type">
<#assign arrayClass = "${type.name}Array">
package com.bytefacets.collections.store;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

<#if !type.generic>import com.bytefacets.collections.types.${typeClass};</#if>
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("large")
class Large${type.name}ChunkStoreTest {
<#if type.generic>
    private final Large${type.name}ChunkStore<String> store = new Large${type.name}ChunkStore<>(5, 4);
    private void set(final long index, final String value) {
        store.set${type.name}(index, value);
    }

    private String get(final long index) {
        return store.get${type.name}(index);
    }

    private String convert(final String value) {
        return value;
    }
<#else>
    private final Large${type.name}ChunkStore store = new Large${type.name}ChunkStore(5, 3);
    private void set(final long index, final ${type.javaType} value) {
        store.set${type.name}(index, value);
    }

    private ${type.javaType} get(final long index) {
        return store.get${type.name}(index);
    }

    private ${type.javaType} convert(final String value) {
        return ${typeClass}.parseString(value);
    }
</#if>

    @AfterEach
    void releaseMemory() {
        store.close();
    }

    @Test
    void shouldRoundTripValue() {
        final var value = convert("7");
        set(3, value);
        assertThat(get(3), equalTo(value));
    }

    @Test
    void shouldGrowAtCapacity() {
        final var value = convert("7");
        final long index = store.getCapacity();
        set(index, value);
        assertThat(get(index), equalTo(value));
    }

    @Test
    void shouldGrowWhenLarge() {
        try(Large${type.name}ChunkStore largeStore = new Large${type.name}ChunkStore(Integer.MAX_VALUE, Integer.MAX_VALUE/512)) {
            long index = ((long)Integer.MAX_VALUE) + 1L;
            largeStore.set${type.name}(index, convert("7"));
            assertThat(largeStore.get${type.name}(index), equalTo(convert("7")));
        }
    }

    @ParameterizedTest
    @CsvSource({"6,8","11,12","41,44"})
    void shouldGrowToAccommodateNewIndex(final long index, final long expectedCapacity) {
        set(index, convert("7"));
        assertThat(store.numChunks(), equalTo((int)(expectedCapacity/4)));
        assertThat(store.getCapacity(), equalTo(expectedCapacity));
        assertThat(get(index), equalTo(convert("7")));
    }
}