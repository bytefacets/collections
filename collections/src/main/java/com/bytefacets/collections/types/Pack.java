package com.bytefacets.collections.types;

public final class // SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
Pack {
    private Pack() {}

    public static long packToLong(final int hi, final int lo) {
        long ret = hi;
        ret <<= 32;
        ret |= (lo & 0x00000000FFFFFFFFL);
        return ret;
    }

    public static int unpackHiInt(final long value) {
        return (int) (value >>> 32);
    }

    public static int unpackLoInt(final long value) {
        return (int) value;
    }

    public static int packToInt(final byte b1, final byte b2, final byte b3, final byte b4) {
        return packToInt(packToShort(b1, b2), packToShort(b3, b4));
    }

    @SuppressWarnings("lossy-conversions")
    public static int packToInt(final short hi, final short lo) {
        int ret = hi;
        ret <<= 16;
        ret |= (lo & 0x0000FFFFL);
        return ret;
    }

    public static short unpackHiShort(final int value) {
        return (short) (value >>> 16);
    }

    public static short unpackLoShort(final int value) {
        return (short) value;
    }

    @SuppressWarnings("lossy-conversions")
    public static short packToShort(final byte hi, final byte lo) {
        short ret = hi;
        ret <<= 8;
        ret |= (lo & 0x00FFL);
        return ret;
    }

    public static byte unpackHiByte(final short value) {
        return (byte) (value >>> 8);
    }

    public static byte unpackLoByte(final short value) {
        return (byte) (value);
    }
}
