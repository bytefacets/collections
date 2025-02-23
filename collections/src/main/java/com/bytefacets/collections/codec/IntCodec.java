package com.bytefacets.collections.codec;

@SuppressWarnings("all")
public final class IntCodec {
    private IntCodec() {}

    public static void encodeIntLE(final int value, final byte[] array, final int offset) {
        array[offset + 3] = (byte) (value >> 24);
        array[offset + 2] = (byte) (value >> 16);
        array[offset + 1] = (byte) (value >> 8);
        array[offset] = (byte) (value & 0xff);
    }

    public static int decodeIntLE(final byte[] array, final int offset) {
        return ((array[offset + 3] & 0xff) << 24)
                | ((array[offset + 2] & 0xff) << 16)
                | ((array[offset + 1] & 0xff) << 8)
                | (array[offset] & 0xff);
    }

    public static void encodeUIntLE(final long value, final byte[] array, final int offset) {
        int asInt = (int) (value & 0xffffffffL);
        array[offset + 3] = (byte) (asInt >> 24);
        array[offset + 2] = (byte) (asInt >> 16);
        array[offset + 1] = (byte) (asInt >> 8);
        array[offset] = (byte) (asInt & 0xff);
    }

    public static long decodeUIntLE(final byte[] array, final int offset) {
        final long val =
                (((long) (array[offset + 3] & 0xff) << 24)
                        | ((array[offset + 2] & 0xff) << 16)
                        | ((array[offset + 1] & 0xff) << 8)
                        | (array[offset] & 0xff));
        return (val & 0xffffffffL);
    }
}
