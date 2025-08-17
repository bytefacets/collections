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

class LongTypeTest {

    @Test
    void shouldUnboxNumbers() {
        Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                .forEach(val -> assertThat(LongType.unbox(val), equalTo(5L)));
    }

    @ParameterizedTest
    @CsvSource({
        "Ascending,3|-4|1|2,-4|1|2|3",
        "Descending,3|-4|1|2,3|2|1|-4",
        "AscendingAbsolute,3|-4|1|2,1|2|3|-4",
        "DescendingAbsolute,3|-4|1|2,-4|3|2|1"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<Long> in =
                Stream.of(input.split("\\|"))
                        .map(LongType::parseString)
                        .collect(Collectors.toList());
        in.sort(LongType.comparatorFor(method)::compare);
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
            final long a,
            final long b,
            final boolean expectedResult) {
        assertThat(LongType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    class FlagTests {
        @ParameterizedTest
        @CsvSource({
            "0o00000000,0o10040007,0o10040007",
            "0o10002001,0o02011001,0o12013001",
            "0o67777777,0o00000000,0o67777777",
            "0o00000000,0o67777777,0o67777777"
        })
        void shouldEnableFlags(final String value, final String bits, final String result) {
            final long bResult = Long.parseLong(result.substring(2), 8);
            final long bValue = Long.parseLong(value.substring(2), 8);
            final long bBits = Long.parseLong(bits.substring(2), 8);
            assertThat(LongType.enableBits(bValue, bBits), equalTo(bResult));
        }

        @ParameterizedTest
        @CsvSource({
            "0o00000000,0o10040007,0o00000000",
            "0o03011001,0o12002001,0o01011000",
            "0o67777777,0o00000000,0o67777777",
            "0o00000000,0o67777777,0o00000000"
        })
        void shouldDisableFlags(final String value, final String bits, final String result) {
            final long bResult = Long.parseLong(result.substring(2), 8);
            final long bValue = Long.parseLong(value.substring(2), 8);
            final long bBits = Long.parseLong(bits.substring(2), 8);
            assertThat(LongType.disableBits(bValue, bBits), equalTo(bResult));
        }
    }

    @Nested
    class ReadWriteArrayTests {
        private final byte[] array = new byte[10];
        private final byte[] bufArr = new byte[10];
        private final ByteBuffer buffer = ByteBuffer.wrap(bufArr).position(1);

        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, Long.MAX_VALUE, 0, -1, 1174259})
        void shouldRoundTripBigEndian(final long value) {
            assertThat(LongType.writeBE(array, 1, value), equalTo(9));
            assertThat(LongType.readBE(array, 1), equalTo(value));

            buffer.order(ByteOrder.BIG_ENDIAN).putLong(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }

        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, Long.MAX_VALUE, 0, -1, 1174259})
        void shouldRoundTripLittleEndian(final long value) {
            assertThat(LongType.writeLE(array, 1, value), equalTo(9));
            assertThat(LongType.readLE(array, 1), equalTo(value));

            buffer.order(ByteOrder.LITTLE_ENDIAN).putLong(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                    .forEach(val -> assertThat(LongType.convert(val), equalTo(5L)));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(LongType.convert(null), equalTo(LongType.DEFAULT));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(LongType.convert(true), equalTo(1L));
            assertThat(LongType.convert(false), equalTo(0L));
        }

        @Test
        void shouldConvertString() {
            assertThat(LongType.convert("123"), equalTo(123L));
        }
    }
}
