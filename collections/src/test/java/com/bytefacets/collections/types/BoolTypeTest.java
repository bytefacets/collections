// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
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

class BoolTypeTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldUnboxNumbers() {
        assertThat(BoolType.unbox(Boolean.TRUE), equalTo(true));
        assertThat(BoolType.unbox(Boolean.FALSE), equalTo(false));
    }

    @ParameterizedTest
    @CsvSource({
        "Ascending,true|false|true,true|true|false",
        "Descending,true|false|true,false|true|true"
    })
    void shouldCompare(final CompareMethod method, final String input, final String expected) {
        final List<Boolean> in =
                Stream.of(input.split("\\|"))
                        .map(BoolType::parseString)
                        .collect(Collectors.toList());
        in.sort(BoolType.comparatorFor(method)::compare);
        assertThat(
                in.stream().map(Object::toString).collect(Collectors.joining("|")),
                equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "EQ,false,false,true",
        "EQ,true,true,true",
        "EQ,true,false,false",
        "NEQ,false,false,false",
        "NEQ,true,true,false",
        "NEQ,true,false,true",
    })
    void shouldTestPredicate(
            final PredicateMethod method,
            final boolean a,
            final boolean b,
            final boolean expectedResult) {
        assertThat(BoolType.predicateFor(method).test(a, b), equalTo(expectedResult));
    }

    @Nested
    @SuppressFBWarnings({"SIC_THREADLOCAL_DEADLY_EMBRACE", "SIC_INNER_SHOULD_BE_STATIC"})
    class ConvertTests {
        @Test
        void shouldConvertNumbers() {
            Stream.of(Byte.parseByte("5"), Short.parseShort("5"), 5, 5L, 5.2f, 5.3d)
                    .forEach(val -> assertThat(BoolType.convert(val), equalTo(true)));
            assertThat(BoolType.convert(0), equalTo(false));
        }

        @Test
        void shouldConvertString() {
            assertThat(BoolType.convert("true"), equalTo(true));
            assertThat(BoolType.convert("false"), equalTo(false));
        }

        @Test
        void shouldConvertNullToDefaultValue() {
            assertThat(BoolType.convert(null), equalTo(BoolType.DEFAULT));
        }

        @Test
        void shouldConvertBoolean() {
            assertThat(BoolType.convert(true), equalTo(true));
            assertThat(BoolType.convert(false), equalTo(false));
        }
    }
}
