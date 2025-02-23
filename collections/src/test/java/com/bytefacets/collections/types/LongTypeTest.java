package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
