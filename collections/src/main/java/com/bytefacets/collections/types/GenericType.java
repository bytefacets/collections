// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

public final class GenericType {
    public static final Object DEFAULT = null;

    private GenericType() {}

    public interface Eq {
        boolean areEqual(Object a, Object b);
    }

    public static final Eq EqImpl = Objects::equals;

    public interface Cmp {
        int compare(Object a, Object b);
    }

    public static final Cmp Asc = GenericType::nullableCompare;
    public static final Cmp Desc = (a, b) -> nullableCompare(b, a);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SuppressFBWarnings(
            value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ",
            justification = "this is a short-circuit for identity and null comparison")
    private static int nullableCompare(final Object a, final Object b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        final Comparable aCmp = (Comparable) a;
        return aCmp.compareTo(b);
    }

    public interface Pred {
        boolean test(String a, String b);
    }

    public static final Pred IS_EQ = Objects::equals;
    public static final Pred IS_NEQ = (a, b) -> !Objects.equals(a, b);

    public static Pred predicateFor(final PredicateMethod method) {
        return switch (method) {
            case EQ -> IS_EQ;
            case NEQ -> IS_NEQ;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Hash {
        int code(Object v);
    }

    public static final Hash HashImpl = (v) -> v != null ? v.hashCode() : 0;

    public static int castToInt(final Object value) {
        return Integer.parseInt(value.toString());
    }

    public static Object castToGeneric(final int value) {
        return value;
    }

    public static Object unbox(final Object value) {
        return value;
    }

    public static Object box(final Object value) {
        return value;
    }

    public static Object parseString(final String value) {
        return value;
    }

    public static Object convert(final Object value) {
        return value;
    }
}
