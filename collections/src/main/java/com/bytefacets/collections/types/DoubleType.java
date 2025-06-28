// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class DoubleType {
    public static final double DEFAULT = 0;

    private DoubleType() {}

    public interface Eq {
        boolean areEqual(double a, double b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;
    public static final Eq EqEpsilon = (a, b) -> Math.abs(a - b) < 1e-10;

    public interface Cmp {
        int compare(double a, double b);
    }

    public static final Cmp Asc = (a, b) -> (int) Math.signum(a - b);
    public static final Cmp Desc = (a, b) -> (int) Math.signum(b - a);
    public static final Cmp AbsAsc = (a, b) -> Asc.compare(Math.abs(a), Math.abs(b));
    public static final Cmp AbsDesc = (a, b) -> Desc.compare(Math.abs(a), Math.abs(b));

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
        boolean test(double a, double b);
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
        int code(double v);
    }

    public static final Hash HashImpl = Double::hashCode;

    public static int castToInt(final double value) {
        return (int) value;
    }

    public static double castToDouble(final int value) {
        return value;
    }

    public static double unbox(final Object value) {
        return value != null ? ((Number) value).doubleValue() : DEFAULT;
    }

    public static Double box(final double value) {
        return value;
    }

    public static double parseString(final String value) {
        return Double.parseDouble(value);
    }

    public static double convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).doubleValue();
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return (short) (((Boolean) value) ? 1 : 0);
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to long: %s (%s)", value, type.getName()));
    }
}
