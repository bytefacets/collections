<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
<#assign typeClass = "${type.name}Type">
<#assign arrayClass = "${type.name}Array">
package com.bytefacets.collections.store;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

<#if !type.generic>import com.bytefacets.collections.types.${typeClass};</#if>
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ${type.name}ChunkStoreTest {
<#if type.generic>
    private final ${type.name}ChunkStore<String> store = new ${type.name}ChunkStore<>(5, 4);
    private void set(final int index, final String value) {
        store.set${type.name}(index, value);
    }

    private String get(final int index) {
        return store.get${type.name}(index);
    }

    private String convert(final String value) {
        return value;
    }
<#else>
    private final ${type.name}ChunkStore store = new ${type.name}ChunkStore(5, 3);
    private void set(final int index, final ${type.javaType} value) {
        store.set${type.name}(index, value);
    }

    private ${type.javaType} get(final int index) {
        return store.get${type.name}(index);
    }

    private ${type.javaType} convert(final String value) {
        return ${typeClass}.parseString(value);
    }
</#if>

    @Test
    void shouldRoundTripValue() {
        final var value = convert("7");
        set(3, value);
        assertThat(get(3), equalTo(value));
    }

    @Test
    void shouldGrowAtCapacity() {
        final var value = convert("7");
        final int index = store.getCapacity();
        set(index, value);
        assertThat(get(index), equalTo(value));
    }

    @ParameterizedTest
    @CsvSource({"6,8","11,12","41,44"})
    void shouldGrowToAccommodateNewIndex(final int index, final int expectedCapacity) {
        set(index, convert("7"));
        assertThat(store.numChunks(), equalTo(expectedCapacity/4));
        assertThat(store.getCapacity(), equalTo(expectedCapacity));
        assertThat(get(index), equalTo(convert("7")));
    }
}