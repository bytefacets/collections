<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
<#assign typeClass = "${type.name}Type">
<#assign arrayClass = "${type.name}Array">
package com.bytefacets.collections.store;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;

import com.bytefacets.collections.exception.RangeCheckException;

<#if !type.generic>import com.bytefacets.collections.types.${typeClass};</#if>
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ${type.name}ChunkMatrixStoreTest {
    private static final int CHUNK_SIZE = 16;
    private static final int NUM_FIELDS = 3;
<#if type.generic>
    private final ${type.name}ChunkMatrixStore<String> store = new ${type.name}ChunkMatrixStore<>(5, CHUNK_SIZE, NUM_FIELDS);
    private void set(final int index, final int field, final String value) {
        store.set${type.name}(index, field, value);
    }

    private String get(final int index, final int field) {
        return store.get${type.name}(index, field);
    }

    private String convert(final String value) {
        return value;
    }
<#else>
    private final ${type.name}ChunkMatrixStore store = new ${type.name}ChunkMatrixStore(5, CHUNK_SIZE, NUM_FIELDS);
    private void set(final int index, final int field, final ${type.javaType} value) {
        store.set${type.name}(index, field, value);
    }

    private ${type.javaType} get(final int index, final int field) {
        return store.get${type.name}(index, field);
    }

    private ${type.javaType} convert(final String value) {
        return ${typeClass}.parseString(value);
    }
</#if>

    @Test
    void shouldRoundTripValues() {
        set(3, 1, convert("7"));
        set(2, 2, convert("8"));
        set(1, 0, convert("9"));
        assertThat(get(3, 1), equalTo(convert("7")));
        assertThat(get(2, 2), equalTo(convert("8")));
        assertThat(get(1, 0), equalTo(convert("9")));
    }

    @ParameterizedTest
    @CsvSource({"6,2,10", "11,3,16", "42,9,48"})
    void shouldGrowToAccommodateNewIndex(final int index, final int expectedChunks, final int expectedCapacity) {
        set(index, 2, convert("7"));
        assertThat(store.getCapacity(), equalTo(expectedCapacity));
        assertThat(store.numChunks(), equalTo(expectedChunks));
        assertThat(get(index, 2), equalTo(convert("7")));
    }

    @Test
    void shouldThrowWhenRequestingNegativeField() {
        assertThrows(RangeCheckException.class, () -> get(1, -1));
        assertThrows(RangeCheckException.class, () -> set(1, -1, convert("7")));
    }

    @Test
    void shouldThrowWhenRequestingFieldLargerThanConfigured() {
        assertThrows(RangeCheckException.class, () -> get(1, NUM_FIELDS));
        assertThrows(RangeCheckException.class, () -> set(1, NUM_FIELDS, convert("7")));
    }
}