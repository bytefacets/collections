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

class ByteTypeTest {

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
        final List<Byte> in =
                Stream.of(input.split("\\|"))
                        .map(ByteType::parseString)
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
            final byte a,
            final byte b,
            final boolean expectedResult) {
        assertThat(ByteType.predicateFor(method).test(a, b), equalTo(expectedResult));
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
            final byte bResult = (byte) Integer.parseInt(result.substring(2), 2);
            final byte bValue = (byte) Integer.parseInt(value.substring(2), 2);
            final byte bBits = (byte) Integer.parseInt(bits.substring(2), 2);
            assertThat(ByteType.enableBits(bValue, bBits), equalTo(bResult));
        }

        @ParameterizedTest
        @CsvSource({
            "0b00000000,0b10010001,0b00000000",
            "0b10001001,0b00011000,0b10000001",
            "0b11111111,0b00000000,0b11111111",
            "0b00000000,0b11111111,0b00000000"
        })
        void shouldDisableFlags(final String value, final String bits, final String result) {
            final byte bResult = (byte) Integer.parseInt(result.substring(2), 2);
            final byte bValue = (byte) Integer.parseInt(value.substring(2), 2);
            final byte bBits = (byte) Integer.parseInt(bits.substring(2), 2);
            assertThat(ByteType.disableBits(bValue, bBits), equalTo(bResult));
        }
    }

    @Nested
    class ReadWriteArrayTests {
        private final byte[] array = new byte[3];
        private final byte[] bufArr = new byte[3];
        private final ByteBuffer buffer = ByteBuffer.wrap(bufArr).position(1);

        @ParameterizedTest
        @ValueSource(bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE, 0, -1, 117})
        void shouldRoundTripBigEndian(final byte value) {
            assertThat(ByteType.writeBE(array, 1, value), equalTo(2));
            assertThat(ByteType.readBE(array, 1), equalTo(value));

            buffer.order(ByteOrder.BIG_ENDIAN).put(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }

        @ParameterizedTest
        @ValueSource(bytes = {Byte.MIN_VALUE, Byte.MAX_VALUE, 0, -1, 117})
        void shouldRoundTripLittleEndian(final byte value) {
            assertThat(ByteType.writeLE(array, 1, value), equalTo(2));
            assertThat(ByteType.readLE(array, 1), equalTo(value));

            buffer.order(ByteOrder.LITTLE_ENDIAN).put(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                    .forEach(val -> assertThat(ByteType.convert(val), equalTo((byte) 5)));
        }

        @Test
        void shouldConvertString() {
            assertThat(ByteType.convert("123"), equalTo((byte) 123));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(ByteType.convert(null), equalTo(ByteType.DEFAULT));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(ByteType.convert(true), equalTo((byte) 1));
            assertThat(ByteType.convert(false), equalTo((byte) 0));
        }
    }
}
