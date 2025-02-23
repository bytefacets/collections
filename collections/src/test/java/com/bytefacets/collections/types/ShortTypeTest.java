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
