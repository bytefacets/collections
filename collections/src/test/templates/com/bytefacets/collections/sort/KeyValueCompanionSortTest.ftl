<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.sort;

import com.bytefacets.collections.types.${key.name}Type;
<#if key != value>
import com.bytefacets.collections.types.${value.name}Type;
</#if>
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ${key.name}${value.name}CompanionSortTest {
    private final Random random = new Random(537638739);
    private ${key.arrayType}[] primary;
    private ${value.arrayType}[] secondary;

    @Test
    void shouldSortAscendingSmallerSize() {
        produceArrays(7);
        assertAscendingSort(primary, secondary);
    }

    @Test
    void shouldSortDescendingSmallerSize() {
        produceArrays(7);
        assertDescendingSort(primary, secondary);
    }

    @Test
    void shouldSortAscendingLargerSize() {
        produceArrays(99);
        assertAscendingSort(primary, secondary);
    }

    @Test
    void shouldSortDescendingLargerSize() {
        produceArrays(99);
        assertDescendingSort(primary, secondary);
    }

    @Test
    void shouldSortAscendingWithDuplicates() {
        produceArraysWithDuplicates(99);
        ${key.name}${value.name}CompanionSort.sort(primary, secondary, 0, primary.length, ${key.name}Type.Asc);

        ${key.arrayType} prevP = castK(0);
        for(int i = 0; i < primary.length; i++) {
            assertThat(info(prevP, primary[i], i), ${key.name}Type.Asc.compare(prevP, primary[i]), lessThanOrEqualTo(0));
            prevP = primary[i];
        }
    }

    @Test
    void shouldSortDescendingWithDuplicates() {
        produceArraysWithDuplicates(99);
        ${key.name}${value.name}CompanionSort.sort(primary, secondary, 0, primary.length, ${key.name}Type.Desc);

        ${key.arrayType} prevP = castK(100);
        for(int i = 0; i < primary.length; i++) {
            assertThat(info(prevP, primary[i], i), ${key.name}Type.Desc.compare(prevP, primary[i]), lessThanOrEqualTo(0));
            prevP = primary[i];
        }
    }

    @ParameterizedTest
    @CsvSource({"-1,2", "3,3", "5,2", "2,-1", "1,4"})
    void shouldThrowWhenInvalidFromTo(final int from, final int to) {
        primary = new ${key.arrayType}[5];
        secondary = new ${value.arrayType}[4];

        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                ${key.name}${value.name}CompanionSort.sort(primary, secondary, from, to, ${key.name}Type.Asc));
        assertThat(ex.getMessage(), containsString(Integer.toString(from)));
        assertThat(ex.getMessage(), containsString(Integer.toString(to)));
    }

    private void assertAscendingSort(final ${key.arrayType}[] primary, final ${value.arrayType}[] secondary) {
        ${key.name}${value.name}CompanionSort.sort(primary, secondary, 0, primary.length, ${key.name}Type.Asc);

        ${key.arrayType} prevP = castK(0);
<#if value.name == "Bool">
        ${value.arrayType} prevS = false;
<#else>
        ${value.arrayType} prevS = castV(100);
</#if>
        for(int i = 0; i < primary.length; i++) {
            assertThat(info(prevP, primary[i], i), ${key.name}Type.Asc.compare(prevP, primary[i]), lessThanOrEqualTo(0));
            // came along for the ride, but is in the opposite direction
            assertThat(info(prevS, secondary[i], i), ${value.name}Type.Desc.compare(prevS, secondary[i]), lessThanOrEqualTo(0));

            prevP = primary[i];
            prevS = secondary[i];
        }
    }

    private void assertDescendingSort(final ${key.arrayType}[] primary, final ${value.arrayType}[] secondary) {
        ${key.name}${value.name}CompanionSort.sort(primary, secondary, 0, primary.length, ${key.name}Type.Desc);

        ${key.arrayType} prevP = castK(100);
<#if value.name == "Bool">
        ${value.arrayType} prevS = true;
<#else>
        ${value.arrayType} prevS = castV(0);
</#if>
        for(int i = 0; i < primary.length; i++) {
            assertThat(info(prevP, primary[i], i), ${key.name}Type.Desc.compare(prevP, primary[i]), lessThanOrEqualTo(0));
            // came along for the ride, but is in the opposite direction
            assertThat(info(prevS, secondary[i], i), ${value.name}Type.Asc.compare(prevS, secondary[i]), lessThanOrEqualTo(0));

            prevP = primary[ i];
            prevS = secondary[i];
        }
    }

    private ${key.arrayType} castK(int key) {
<#if key.name == "String">
        return String.format("%03d", key);
<#else>
        return ${key.name}Type.convert(key);
</#if>
    }

    private ${value.arrayType} castV(int key) {
<#if value.name == "String">
        return String.format("%03d", key);
<#else>
        return ${value.name}Type.convert(key);
</#if>
}

    private void produceArrays(final int size) {
        final List<Integer> indexes = new ArrayList<>(size);
        for(int i = 0; i < size; i++) indexes.add(i);
        Collections.shuffle(indexes, random);
        primary = new ${key.arrayType}[size];
        secondary = new ${value.arrayType}[size];
        for(int i = 0; i < size; i++) {
            final int index = indexes.get(i);
            primary[i] = castK(size - index + 1);
            secondary[i] = castV(index + 1);
        }
    }

    private void produceArraysWithDuplicates(final int size) {
        final List<Integer> indexes = new ArrayList<>(size);
        for(int i = 0; i < size; i++) indexes.add(i);
        Collections.shuffle(indexes, random);
        primary = new ${key.arrayType}[size];
        secondary = new ${value.arrayType}[size];
        for(int i = 0; i < size; i++) {
            final int index = indexes.get(i);
            primary[i] = castK((size - index + 1) % 7);
            secondary[i] = castV(index + 1);
        }
    }

    private String info(Object prev, Object cur, int position) {
        return String.format("position[%d]=%s vs prev=%s", position, cur, prev);
    }
}
