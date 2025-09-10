// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.types;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

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

    public static int enableBits(final int flag, final int bits) {
        return flag | bits;
    }

    public static int disableBits(final int flag, final int bits) {
        return flag & ~bits;
    }

    public static int writeLE(final byte[] array, final int pos, final int value) {
        array[pos + 3] = (byte) (value >> 24);
        array[pos + 2] = (byte) (value >> 16);
        array[pos + 1] = (byte) (value >> 8);
        array[pos] = (byte) (value & 0xff);
        return pos + 4;
    }

    public static int writeBE(final byte[] array, final int pos, final int value) {
        array[pos] = (byte) (value >> 24);
        array[pos + 1] = (byte) (value >> 16);
        array[pos + 2] = (byte) (value >> 8);
        array[pos + 3] = (byte) (value & 0xff);
        return pos + 4;
    }

    @SuppressWarnings("BooleanExpressionComplexity")
    public static int readLE(final byte[] array, final int pos) {
        return (((array[pos + 3] & 0xff) << 24)
                | ((array[pos + 2] & 0xff) << 16)
                | ((array[pos + 1] & 0xff) << 8)
                | (array[pos] & 0xff));
    }

    @SuppressWarnings("BooleanExpressionComplexity")
    public static int readBE(final byte[] array, final int pos) {
        return (((array[pos] & 0xff) << 24)
                | ((array[pos + 1] & 0xff) << 16)
                | ((array[pos + 2] & 0xff) << 8)
                | (array[pos + 3] & 0xff));
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
        return toYYYMMDD(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static LocalDate toLocalDate(int yyyyMMdd) {
        final int year = yyyyMMdd / 10000;
        final int month = (yyyyMMdd / 100) % 100;
        final int day = yyyyMMdd % 100;
        return LocalDate.of(year, month, day);
    }

    public static int fromLocalTime(final LocalTime time) {
        return toHHMMSS(time.getHour(), time.getMinute(), time.getSecond());
    }

    @SuppressWarnings("deprecation")
    public static int fromSqlDate(final Date date) {
        // e.g. date.toLocalDate()
        return toYYYMMDD(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public static LocalTime toLocalTime(int hhMMss) {
        final int hours = hhMMss / 10000;
        final int minutes = (hhMMss / 100) % 100;
        final int seconds = hhMMss % 100;
        return LocalTime.of(hours, minutes, seconds);
    }

    public static int toYYYMMDD(final int year, final int month, final int day) {
        return (year * 10000) + (month * 100) + day;
    }

    public static int toHHMMSS(final int hour, final int minute, final int second) {
        return (hour * 10000) + (minute * 100) + second;
    }
}
