// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

public final class LongType {
    public static final long DEFAULT = 0;

    private LongType() {}

    public interface Eq {
        boolean areEqual(long a, long b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(long a, long b);
    }

    public static final Cmp Asc = Long::compare;
    public static final Cmp Desc = (a, b) -> Long.compare(b, a);
    public static final Cmp AbsAsc = (a, b) -> Long.compare(Math.abs(a), Math.abs(b));
    public static final Cmp AbsDesc = (a, b) -> Long.compare(Math.abs(b), Math.abs(a));

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
        boolean test(long a, long b);
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
        int code(long v);
    }

    public static final Hash HashImpl = Long::hashCode;

    public static int castToInt(final long value) {
        return (int) value;
    }

    public static long castToLong(final int value) {
        return value;
    }

    public static long unbox(final Object value) {
        return value != null ? ((Number) value).longValue() : DEFAULT;
    }

    public static Long box(final long value) {
        return value;
    }

    public static long parseString(final String value) {
        return Long.parseLong(value);
    }

    public static long enableBits(final long flag, final long bits) {
        return flag | bits;
    }

    public static long disableBits(final long flag, final long bits) {
        return flag & ~bits;
    }

    public static int writeLE(final byte[] array, final int pos, final long value) {
        array[pos + 7] = (byte) (value >> 56);
        array[pos + 6] = (byte) (value >> 48);
        array[pos + 5] = (byte) (value >> 40);
        array[pos + 4] = (byte) (value >> 32);
        array[pos + 3] = (byte) (value >> 24);
        array[pos + 2] = (byte) (value >> 16);
        array[pos + 1] = (byte) (value >> 8);
        array[pos] = (byte) (value & 0xff);
        return 8;
    }

    public static int writeBE(final byte[] array, final int pos, final long value) {
        array[pos] = (byte) (value >> 56);
        array[pos + 1] = (byte) (value >> 48);
        array[pos + 2] = (byte) (value >> 40);
        array[pos + 3] = (byte) (value >> 32);
        array[pos + 4] = (byte) (value >> 24);
        array[pos + 5] = (byte) (value >> 16);
        array[pos + 6] = (byte) (value >> 8);
        array[pos + 7] = (byte) (value & 0xff);
        return 8;
    }

    public static long readLE(final byte[] array, final int pos) {
        return ((((long) array[pos + 7] & 0xff) << 56)
                | (((long) array[pos + 6] & 0xff) << 48)
                | (((long) array[pos + 5] & 0xff) << 40)
                | (((long) array[pos + 4] & 0xff) << 32)
                | (((long) array[pos + 3] & 0xff) << 24)
                | (((long) array[pos + 2] & 0xff) << 16)
                | (((long) array[pos + 1] & 0xff) << 8)
                | (((long) array[pos] & 0xff)));
    }

    public static long readBE(final byte[] array, final int pos) {
        return ((((long) array[pos] & 0xff) << 56)
                | (((long) array[pos + 1] & 0xff) << 48)
                | (((long) array[pos + 2] & 0xff) << 40)
                | (((long) array[pos + 3] & 0xff) << 32)
                | (((long) array[pos + 4] & 0xff) << 24)
                | (((long) array[pos + 5] & 0xff) << 16)
                | (((long) array[pos + 6] & 0xff) << 8)
                | (((long) array[pos + 7] & 0xff)));
    }

    public static long convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).longValue();
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
