package com.bytefacets.collections.codec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntCodecTest {
    @ParameterizedTest
    @ValueSource(ints = {0xA6786729, 0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 28728})
    void shouldRoundTrip(final int value) {
        final var array = new byte[8];
        IntCodec.encodeIntLE(value, array, 2);
        assertThat(IntCodec.decodeIntLE(array, 2), equalTo(value));
    }
}
