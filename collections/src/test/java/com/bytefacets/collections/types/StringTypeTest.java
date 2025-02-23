package com.bytefacets.collections.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringTypeTest {

    @ParameterizedTest
    @CsvSource({
        "Ascending,a3|A4|a1|a2,A4|a1|a2|a3",
        "Descending,a3|A4|a1|a2,a3|a2|a1|A4",
        "AscendingAbsolute,a3|A4|a1|a2,a1|a2|a3|A4",
        "DescendingAbsolute,a3|A4|a1|a2,A4|a3|a2|a1"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<String> in = Arrays.asList(input.split("\\|"));
        in.sort(StringType.comparatorFor(method)::compare);
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
            final String a,
            final String b,
            final boolean expectedResult) {
        assertThat(StringType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(StringType.convert(null), equalTo(StringType.DEFAULT));
        }

        @Test
        void shouldConvertString() {
            assertThat(StringType.convert("123"), equalTo("123"));
        }
    }
}
