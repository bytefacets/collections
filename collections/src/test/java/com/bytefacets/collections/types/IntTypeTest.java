// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class IntTypeTest {

    @Test
    void shouldUnboxNumbers() {
        Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                .forEach(val -> assertThat(IntType.unbox(val), equalTo(5)));
    }

    @ParameterizedTest
    @CsvSource({
        "Ascending,3|-4|1|2,-4|1|2|3",
        "Descending,3|-4|1|2,3|2|1|-4",
        "AscendingAbsolute,3|-4|1|2,1|2|3|-4",
        "DescendingAbsolute,3|-4|1|2,-4|3|2|1"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<Integer> in =
                Stream.of(input.split("\\|"))
                        .map(IntType::parseString)
                        .collect(Collectors.toList());
        in.sort(IntType.comparatorFor(method)::compare);
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
            final PredicateMethod method, final int a, final int b, final boolean expectedResult) {
        assertThat(IntType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    class FlagTests {
        @ParameterizedTest
        @CsvSource({
            "0b00000000000000000000000000000000,0b01010001100100011001000110010001,0b01010001100100011001000110010001",
            "0b00101001100010011000100110001001,0b01010001000100010001000100010001,0b01111001100110011001100110011001",
            "0b01111111111111111111111111111111,0b00000000000000000000000000000000,0b01111111111111111111111111111111",
            "0b00000000000000000000000000000000,0b01111111111111111111111111111111,0b01111111111111111111111111111111"
        })
        void shouldEnableFlags(final String value, final String bits, final String result) {
            final int bResult = Integer.parseInt(result.substring(2), 2);
            final int bValue = Integer.parseInt(value.substring(2), 2);
            final int bBits = Integer.parseInt(bits.substring(2), 2);
            assertThat(IntType.enableBits(bValue, bBits), equalTo(bResult));
        }

        @ParameterizedTest
        @CsvSource({
            "0b00000000000000000000000000000000,0b00010001100100011001000110010001,0b00000000000000000000000000000000",
            "0b00001001100010011000100110001001,0b00011000000110000001100000011000,0b00000001100000011000000110000001",
            "0b01111111111111111111111111111111,0b00000000000000000000000000000000,0b01111111111111111111111111111111",
            "0b00000000000000000000000000000000,0b01111111111111111111111111111111,0b00000000000000000000000000000000"
        })
        void shouldDisableFlags(final String value, final String bits, final String result) {
            final int bResult = Integer.parseInt(result.substring(2), 2);
            final int bValue = Integer.parseInt(value.substring(2), 2);
            final int bBits = Integer.parseInt(bits.substring(2), 2);
            assertThat(IntType.disableBits(bValue, bBits), equalTo(bResult));
        }
    }

    @Nested
    class ReadWriteArrayTests {
        private final byte[] array = new byte[6];
        private final byte[] bufArr = new byte[6];
        private final ByteBuffer buffer = ByteBuffer.wrap(bufArr).position(1);

        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1174259})
        void shouldRoundTripBigEndian(final int value) {
            assertThat(IntType.writeBE(array, 1, value), equalTo(4));
            assertThat(IntType.readBE(array, 1), equalTo(value));

            buffer.order(ByteOrder.BIG_ENDIAN).putInt(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }

        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, Integer.MAX_VALUE, 0, -1, 1174259})
        void shouldRoundTripLittleEndian(final int value) {
            assertThat(IntType.writeLE(array, 1, value), equalTo(4));
            assertThat(IntType.readLE(array, 1), equalTo(value));

            buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                    .forEach(val -> assertThat(IntType.convert(val), equalTo(5)));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(IntType.convert(true), equalTo(1));
            assertThat(IntType.convert(false), equalTo(0));
        }

        @Test
        void shouldConvertString() {
            assertThat(IntType.convert("123"), equalTo(123));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(IntType.convert(null), equalTo(IntType.DEFAULT));
        }

        @Test
        void shouldConvertLocalDateToInt() {
            assertThat(IntType.convert(LocalDate.of(2015, 4, 12)), equalTo(20150412));
        }

        @Test
        void shouldConvertLocalTimeToInt() {
            assertThat(IntType.convert(LocalTime.of(20, 5, 13)), equalTo(200513));
        }
    }
}
