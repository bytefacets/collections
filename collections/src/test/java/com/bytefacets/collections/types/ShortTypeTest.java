// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ShortTypeTest {

    @Test
    void shouldUnboxNumbers() {
        Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                .forEach(val -> assertThat(ShortType.unbox(val), equalTo((short) 5)));
    }

    @ParameterizedTest
    @CsvSource({
        "Ascending,3|-4|1|2,-4|1|2|3",
        "Descending,3|-4|1|2,3|2|1|-4",
        "AscendingAbsolute,3|-4|1|2,1|2|3|-4",
        "DescendingAbsolute,3|-4|1|2,-4|3|2|1"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<Short> in =
                Stream.of(input.split("\\|"))
                        .map(ShortType::parseString)
                        .collect(Collectors.toList());
        in.sort(ShortType.comparatorFor(method)::compare);
        assertThat(
                in.stream().map(Object::toString).collect(Collectors.joining("|")),
                equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "EQ,1,1,true",
        "EQ,1,0,false",
        "NEQ,1,1,false",
        "NEQ,1,0,true",
        "LT,1,1,false",
        "LT,1,0,false",
        "LT,0,1,true",
        "LTE,1,1,true",
        "LTE,1,0,false",
        "LTE,0,1,true",
        "GT,1,1,false",
        "GT,1,0,true",
        "GT,0,1,false",
        "GTE,1,1,true",
        "GTE,1,0,true",
        "GTE,0,1,false"
    })
    void shouldTestPredicate(
            final PredicateMethod method,
            final short a,
            final short b,
            final boolean expectedResult) {
        assertThat(ShortType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    class FlagTests {
        @ParameterizedTest
        @CsvSource({
            "0b00000000,0b10010001,0b10010001",
            "0b10001001,0b00010001,0b10011001",
            "0b11111111,0b00000000,0b11111111",
            "0b00000000,0b11111111,0b11111111"
        })
        void shouldEnableFlags(final String value, final String bits, final String result) {
            final short bResult = (short) Integer.parseInt(result.substring(2), 2);
            final short bValue = (short) Integer.parseInt(value.substring(2), 2);
            final short bBits = (short) Integer.parseInt(bits.substring(2), 2);
            assertThat(ShortType.enableBits(bValue, bBits), equalTo(bResult));
        }

        @ParameterizedTest
        @CsvSource({
            "0b00000000,0b10010001,0b00000000",
            "0b10001001,0b00011000,0b10000001",
            "0b11111111,0b00000000,0b11111111",
            "0b00000000,0b11111111,0b00000000"
        })
        void shouldDisableFlags(final String value, final String bits, final String result) {
            final short bResult = (short) Integer.parseInt(result.substring(2), 2);
            final short bValue = (short) Integer.parseInt(value.substring(2), 2);
            final short bBits = (short) Integer.parseInt(bits.substring(2), 2);
            assertThat(ShortType.disableBits(bValue, bBits), equalTo(bResult));
        }
    }

    @Nested
    class ReadWriteArrayTests {
        private final byte[] array = new byte[5];
        private final byte[] bufArr = new byte[5];
        private final ByteBuffer buffer = ByteBuffer.wrap(bufArr).position(1);

        @ParameterizedTest
        @ValueSource(shorts = {Short.MIN_VALUE, Short.MAX_VALUE, 0, -1, 11749})
        void shouldRoundTripBigEndian(final short value) {
            assertThat(ShortType.writeBE(array, 1, value), equalTo(2));
            assertThat(ShortType.readBE(array, 1), equalTo(value));

            buffer.order(ByteOrder.BIG_ENDIAN).putShort(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }

        @ParameterizedTest
        @ValueSource(shorts = {Short.MIN_VALUE, Short.MAX_VALUE, 0, -1, 11749})
        void shouldRoundTripLittleEndian(final short value) {
            assertThat(ShortType.writeLE(array, 1, value), equalTo(2));
            assertThat(ShortType.readLE(array, 1), equalTo(value));

            buffer.order(ByteOrder.LITTLE_ENDIAN).putShort(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                    .forEach(val -> assertThat(ShortType.convert(val), equalTo((short) 5)));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(ShortType.convert(null), equalTo(ShortType.DEFAULT));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(ShortType.convert(true), equalTo((short) 1));
            assertThat(ShortType.convert(false), equalTo((short) 0));
        }

        @Test
        void shouldConvertString() {
            assertThat(ShortType.convert("123"), equalTo((short) 123));
        }
    }
}
