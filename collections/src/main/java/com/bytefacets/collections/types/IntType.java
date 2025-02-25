// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import java.time.LocalDate;
import java.time.LocalTime;

public final class IntType {
    public static final int DEFAULT = 0;

    private IntType() {}

    public interface Eq {
        boolean areEqual(int a, int b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(int a, int b);
    }

    public static final Cmp Asc = (a, b) -> a - b;
    public static final Cmp Desc = (a, b) -> b - a;
    public static final Cmp AbsAsc = (a, b) -> Math.abs(a) - Math.abs(b);
    public static final Cmp AbsDesc = (a, b) -> Math.abs(b) - Math.abs(a);

    public static Cmp comparatorFor(final CompareMethod method) {
        return switch (method) {
            case Ascending -> Asc;
            case Descending -> Desc;
            case AscendingAbsolute -> AbsAsc;
            case DescendingAbsolute -> AbsDesc;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Pred {
        boolean test(int a, int b);
    }

    public static final Pred IS_EQ = (a, b) -> a == b;
    public static final Pred IS_NEQ = (a, b) -> a != b;
    public static final Pred IS_LT = (a, b) -> a < b;
    public static final Pred IS_GT = (a, b) -> a > b;
    public static final Pred IS_LTE = (a, b) -> a <= b;
    public static final Pred IS_GTE = (a, b) -> a >= b;

    public static Pred predicateFor(final PredicateMethod method) {
        return switch (method) {
            case EQ -> IS_EQ;
            case NEQ -> IS_NEQ;
            case LT -> IS_LT;
            case LTE -> IS_LTE;
            case GT -> IS_GT;
            case GTE -> IS_GTE;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Hash {
        int code(int v);
    }

    public static final Hash HashImpl = (v) -> v;

    public static int castToInt(final int value) {
        return value;
    }

    public static int unbox(final Object value) {
        return value != null ? ((Number) value).intValue() : DEFAULT;
    }

    public static Integer box(final int value) {
        return value;
    }

    public static int parseString(final String value) {
        return Integer.parseInt(value);
    }

    public static int convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).intValue();
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            return fromLocalDate((LocalDate) value);
        }
        if (LocalTime.class.isAssignableFrom(type)) {
            return fromLocalTime((LocalTime) value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return ((Boolean) value) ? 1 : 0;
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to int: %s (%s)", value, type.getName()));
    }

    public static int fromLocalDate(final LocalDate date) {
        return (date.getYear() * 10000) + (date.getMonthValue() * 100) + date.getDayOfMonth();
    }

    public static int fromLocalTime(final LocalTime time) {
        return (time.getHour() * 10000) + (time.getMinute() * 100) + time.getSecond();
    }
}
