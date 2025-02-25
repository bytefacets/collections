// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NumUtilsTest {

    @ParameterizedTest
    @CsvSource({
        "0,1",
        "1,1",
        "2,2",
        "3,4",
        "31002,32768",
        "45876,65536",
        "287628762,536870912",
        "973741824,1073741824"
    })
    void shouldCalcNextPowerOf2(final int input, final int output) {
        assertThat(NumUtils.nextPowerOf2(input), equalTo(output));
    }
}
