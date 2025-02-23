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

class FloatTypeTest {

    @Test
    void shouldUnboxNumbers() {
        Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L)
                .forEach(val -> assertThat(FloatType.unbox(val), equalTo(5f)));
        Stream.of(5.2f, 5.2d).forEach(val -> assertThat(FloatType.unbox(val), equalTo(5.2f)));
    }

    @ParameterizedTest
    @CsvSource({
        "Ascending,3|-4|1|2,-4|1|2|3",
        "Descending,3|-4|1|2,3|2|1|-4",
        "AscendingAbsolute,3|-4|1|2,1|2|3|-4",
        "DescendingAbsolute,3|-4|1|2,-4|3|2|1"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<Float> in =
                Stream.of(input.split("\\|"))
                        .map(FloatType::parseString)
                        .collect(Collectors.toList());
        in.sort(FloatType.comparatorFor(method)::compare);
        assertThat(
                in.stream()
                        .map(Float::intValue)
                        .map(Object::toString)
                        .collect(Collectors.joining("|")),
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
            final float a,
            final float b,
            final boolean expectedResult) {
        assertThat(FloatType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L)
                    .forEach(val -> assertThat(FloatType.unbox(val), equalTo(5f)));
            Stream.of(5.2f, 5.2d).forEach(val -> assertThat(FloatType.unbox(val), equalTo(5.2f)));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(FloatType.convert(null), equalTo(FloatType.DEFAULT));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(FloatType.convert(true), equalTo(1f));
            assertThat(FloatType.convert(false), equalTo(0f));
        }

        @Test
        void shouldConvertString() {
            assertThat(FloatType.convert("123"), equalTo(123f));
        }
    }
}
