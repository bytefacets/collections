// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class CharType {
    public static final char DEFAULT = 0;
    public static final char TRUE = 't';
    public static final char FALSE = 'f';

    private CharType() {}

    public interface Eq {
        boolean areEqual(char a, char b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(char a, char b);
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
        boolean test(char a, char b);
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
        int code(char v);
    }

    public static final Hash HashImpl = (v) -> v;

    public static int castToInt(final char value) {
        return value;
    }

    public static char castToChar(final int value) {
        return (char) value;
    }

    public static char unbox(final Object value) {
        return value != null ? (Character) value : DEFAULT;
    }

    public static Character box(final char value) {
        return value;
    }

    public static char parseString(final String value) {
        return value != null && value.length() > 0 ? value.charAt(0) : DEFAULT;
    }

    public static char convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return (char) ((Number) value).intValue();
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return (((Boolean) value) ? TRUE : FALSE);
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to char: %s (%s)", value, type.getName()));
    }

    public static int writeLE(final byte[] array, final int pos, final char value) {
        array[pos + 1] = (byte) (value >> 8);
        array[pos] = (byte) (value & 0xff);
        return pos + 2;
    }

    public static int writeBE(final byte[] array, final int pos, final char value) {
        array[pos] = (byte) (value >> 8);
        array[pos + 1] = (byte) (value & 0xff);
        return pos + 2;
    }

    public static char readLE(final byte[] array, final int pos) {
        return (char) (((array[pos + 1] & 0xff) << 8) | (array[pos] & 0xff));
    }

    public static char readBE(final byte[] array, final int pos) {
        return (char) (((array[pos] & 0xff) << 8) | (array[pos + 1] & 0xff));
    }
}
