// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class PackTest {

    @Test
    void shouldRoundTripIntsInLong() {
        final int[] cases = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1, 87367635};
        for (int hi : cases) {
            for (int lo : cases) {
                final long packed = Pack.packToLong(hi, lo);
                assertThat(Pack.unpackHiInt(packed), equalTo(hi));
                assertThat(Pack.unpackLoInt(packed), equalTo(lo));
            }
        }
    }

    @Test
    void shouldRoundTripShortsInInt() {
        final short[] cases = {Short.MIN_VALUE, Short.MAX_VALUE, 0, -1, 1, 8736};
        for (short hi : cases) {
            for (short lo : cases) {
                final int packed = Pack.packToInt(hi, lo);
                assertThat(Pack.unpackHiShort(packed), equalTo(hi));
                assertThat(Pack.unpackLoShort(packed), equalTo(lo));
            }
        }
    }

    @Test
    void shouldRoundTripBytesInShort() {
        final byte[] cases = {Byte.MIN_VALUE, Byte.MAX_VALUE, 0, -1, 1, 87};
        for (byte hi : cases) {
            for (byte lo : cases) {
                final short packed = Pack.packToShort(hi, lo);
                assertThat(Pack.unpackHiByte(packed), equalTo(hi));
                assertThat(Pack.unpackLoByte(packed), equalTo(lo));
            }
        }
    }

    @Test
    void shouldRoundTripBytesInInt() {
        final byte[] cases = {Byte.MIN_VALUE, Byte.MAX_VALUE, 0, -1, 1, 87};
        for (byte b1 : cases) {
            for (byte b2 : cases) {
                for (byte b3 : cases) {
                    for (byte b4 : cases) {
                        final int packed = Pack.packToInt(b1, b2, b3, b4);
                        final short hi = Pack.unpackHiShort(packed);
                        final short lo = Pack.unpackLoShort(packed);
                        assertThat(Pack.unpackHiByte(hi), equalTo(b1));
                        assertThat(Pack.unpackLoByte(hi), equalTo(b2));
                        assertThat(Pack.unpackHiByte(lo), equalTo(b3));
                        assertThat(Pack.unpackLoByte(lo), equalTo(b4));
                    }
                }
            }
        }
    }
}
