package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CharTypeTest {
    @Nested
    class ReadWriteArrayTests {
        private final byte[] array = new byte[6];
        private final byte[] bufArr = new byte[6];
        private final ByteBuffer buffer = ByteBuffer.wrap(bufArr).position(1);

        @ParameterizedTest
        @ValueSource(chars = {Character.MIN_VALUE, Character.MAX_VALUE, '\0', 'A', (char) 60259})
        void shouldRoundTripBigEndian(final char value) {
            assertThat(CharType.writeBE(array, 1, value), equalTo(3));
            assertThat(CharType.readBE(array, 1), equalTo(value));

            buffer.order(ByteOrder.BIG_ENDIAN).putChar(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }

        @ParameterizedTest
        @ValueSource(chars = {Character.MIN_VALUE, Character.MAX_VALUE, '\0', 'A', (char) 60259})
        void shouldRoundTripLittleEndian(final char value) {
            assertThat(CharType.writeLE(array, 1, value), equalTo(3));
            assertThat(CharType.readLE(array, 1), equalTo(value));

            buffer.order(ByteOrder.LITTLE_ENDIAN).putChar(value);
            assertThat(Arrays.equals(array, bufArr), equalTo(true));
        }
    }
}
