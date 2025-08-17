// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class BoolType {
    public static final boolean DEFAULT = false;

    private BoolType() {}

    public interface Eq {
        boolean areEqual(boolean a, boolean b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(boolean a, boolean b);
    }

    public static final Cmp Asc = (a, b) -> a != b ? a ? -1 : 1 : 0;
    public static final Cmp Desc = (a, b) -> a != b ? a ? 1 : -1 : 0;

    public static Cmp comparatorFor(final CompareMethod method) {
        return switch (method) {
            case Ascending -> Asc;
            case Descending -> Desc;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Pred {
        boolean test(boolean a, boolean b);
    }

    public static final Pred IS_EQ = (a, b) -> a == b;
    public static final Pred IS_NEQ = (a, b) -> a != b;

    public static Pred predicateFor(final PredicateMethod method) {
        return switch (method) {
            case EQ -> IS_EQ;
            case NEQ -> IS_NEQ;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Hash {
        int code(boolean v);
    }

    public static final Hash HashImpl = (v) -> v ? 1 : 0;

    public static int castToInt(final boolean value) {
        return value ? 1 : 0;
    }

    public static boolean castToBool(final int value) {
        return value != 0;
    }

    public static boolean unbox(final Object value) {
        return value != null ? ((Boolean) value) : DEFAULT;
    }

    public static Boolean box(final boolean value) {
        return value;
    }

    public static boolean parseString(final String value) {
        return Boolean.parseBoolean(value);
    }

    public static int writeLE(final byte[] array, final int pos, final boolean value) {
        array[pos] = (byte) (value ? 1 : 0);
        return pos + 1;
    }

    public static int writeBE(final byte[] array, final int pos, final boolean value) {
        array[pos] = (byte) (value ? 1 : 0);
        return pos + 1;
    }

    public static boolean readLE(final byte[] array, final int pos) {
        return array[pos] == 1;
    }

    public static boolean readBE(final byte[] array, final int pos) {
        return array[pos] == 1;
    }

    public static boolean convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Boolean.class.isAssignableFrom(type)) {
            return (Boolean) value;
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).intValue() != 0;
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to boolean: %s (%s)", value, type.getName()));
    }
}
