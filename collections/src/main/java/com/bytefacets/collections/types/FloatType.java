// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class FloatType {
    public static final float DEFAULT = 0;

    private FloatType() {}

    public interface Eq {
        boolean areEqual(float a, float b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(float a, float b);
    }

    public static final Cmp Asc = Float::compare;
    public static final Cmp Desc = (a, b) -> Float.compare(b, a);
    public static final Cmp AbsAsc = (a, b) -> Float.compare(Math.abs(a), Math.abs(b));
    public static final Cmp AbsDesc = (a, b) -> Float.compare(Math.abs(b), Math.abs(a));

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
        boolean test(float a, float b);
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
        int code(float v);
    }

    public static final Hash HashImpl = Float::hashCode;

    public static int castToInt(final float value) {
        return (int) value;
    }

    public static float castToFloat(final int value) {
        return value;
    }

    public static float unbox(final Object value) {
        return value != null ? ((Number) value).floatValue() : DEFAULT;
    }

    public static Float box(final float value) {
        return value;
    }

    public static float parseString(final String value) {
        return Float.parseFloat(value);
    }

    public static float convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).floatValue();
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
