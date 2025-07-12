// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class ShortType {
    public static final short DEFAULT = 0;

    private ShortType() {}

    public interface Eq {
        boolean areEqual(short a, short b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(short a, short b);
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
        boolean test(short a, short b);
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
        int code(short v);
    }

    public static final Hash HashImpl = (v) -> v;

    public static int castToInt(final short value) {
        return value;
    }

    public static short castToShort(final int value) {
        return (short) value;
    }

    public static short unbox(final Object value) {
        return value != null ? ((Number) value).shortValue() : DEFAULT;
    }

    public static Short box(final short value) {
        return value;
    }

    public static short parseString(final String value) {
        return Short.parseShort(value);
    }

    public static short enableBits(final short flag, final short bits) {
        return (short) (flag | bits);
    }

    public static short disableBits(final short flag, final short bits) {
        return (short) (flag & ~bits);
    }

    public static int writeLE(final byte[] array, final int pos, final short value) {
        array[pos + 1] = (byte) (value >> 8);
        array[pos] = (byte) (value & 0xff);
        return 2;
    }

    public static int writeBE(final byte[] array, final int pos, final short value) {
        array[pos] = (byte) (value >> 8);
        array[pos + 1] = (byte) (value & 0xff);
        return 2;
    }

    public static short readLE(final byte[] array, final int pos) {
        return (short) (((array[pos + 1] & 0xff) << 8) | (array[pos] & 0xff));
    }

    public static short readBE(final byte[] array, final int pos) {
        return (short) (((array[pos] & 0xff) << 8) | (array[pos + 1] & 0xff));
    }

    public static short convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).shortValue();
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return (short) (((Boolean) value) ? 1 : 0);
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to short: %s (%s)", value, type.getName()));
    }
}
