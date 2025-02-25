<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
<#assign typeClass = "${type.name}Type">
<#assign arrayClass = "${type.name}Array">
package com.bytefacets.collections.arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.bytefacets.collections.types.${typeClass};
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ${type.name}ArrayTest {
    private final ${type.arrayType}[] array5 = ${arrayClass}.create(5);
    private final ${type.arrayType} value = ${typeClass}.parseString("7");

    @Test
    void shouldCreateNewArray() {
        assertThat(array5.length, equalTo(5));
        for(var val : array5) {
            assertThat(val, equalTo(${typeClass}.DEFAULT));
        }
    }

    @Test
    void shouldFillArray() {
        ${arrayClass}.fill(array5, value);
        for(var val : array5) {
            assertThat(val, equalTo(value));
        }
    }

    @Test
    void shouldFillArrayFromPosition() {
        ${arrayClass}.fill(array5, value, 2, 2);
        for(int i = 0; i < 5; i++) {
            if(i == 2 || i==3) {
                assertThat(array5[i], equalTo(value));
            } else {
                assertThat(array5[i], equalTo(${typeClass}.DEFAULT));
            }
        }
    }

    @Nested
    @SuppressFBWarnings("SIC_THREADLOCAL_DEADLY_EMBRACE")
    class EnsureSizeTests {
        @Test
        void shouldGrowToEnsureSize() {
            final var result = ${arrayClass}.ensureSize(array5, 10);
            assertThat(result.length, equalTo(10));
        }

        @Test
        void shouldMaintainValuesWhenGrowingToEnsureSize() {
            array5[0] = value;
            array5[2] = value;
            array5[4] = value;
            final var result = ${arrayClass}.ensureSize(array5, 10);
            for(int i = 0; i < result.length; i++) {
                if(i == 0 || i == 2 || i == 4) {
                    assertThat(result[i], equalTo(value));
                } else {
                    assertThat(result[i], equalTo(${typeClass}.DEFAULT));
                }
            }
        }

        @Test
        void shouldGrowWithDefaultValues() {
            final var result = ${arrayClass}.ensureSize(array5, 10, value);
            for(int i = 5; i < result.length; i++) {
                assertThat(result[i], equalTo(value));
            }
        }

        @Test
        void shouldReturnSameInstanceWhenNotGrowing() {
            assertThat(${arrayClass}.ensureSize(array5, 5), sameInstance(array5));
        }
    }

    @Nested
    @SuppressFBWarnings("SIC_THREADLOCAL_DEADLY_EMBRACE")
    class EnsureEntryTests {
        @Test
        void shouldGrowToEnsureEntry() {
            final var result = ${arrayClass}.ensureEntry(array5, 10);
            assertThat(result.length, equalTo(20));
        }

        @Test
        void shouldMaintainValuesWhenGrowingToEnsureEntry() {
            array5[0] = value;
            array5[2] = value;
            array5[4] = value;
            final var result = ${arrayClass}.ensureEntry(array5, 10);
            for(int i = 0; i < result.length; i++) {
                if(i == 0 || i == 2 || i == 4) {
                    assertThat(result[i], equalTo(value));
                } else {
                    assertThat(result[i], equalTo(${typeClass}.DEFAULT));
                }
            }
        }

        @Test
        void shouldGrowWithDefaultValues() {
            final var result = ${arrayClass}.ensureEntry(array5, 10, value);
            for(int i = 5; i < result.length; i++) {
                assertThat(result[i], equalTo(value));
            }
        }

        @Test
        void shouldReturnSameInstanceWhenNotGrowing() {
            assertThat(${arrayClass}.ensureEntry(array5, 4), sameInstance(array5));
        }
    }

}