package com.bytefacets.collections.types;

public final class ByteType {
    public static final byte DEFAULT = 0;

    private ByteType() {}

    public interface Eq {
        boolean areEqual(byte a, byte b);
    }

    public static final Eq EqImpl = (a, b) -> a == b;

    public interface Cmp {
        int compare(byte a, byte b);
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
        boolean test(byte a, byte b);
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
        int code(byte v);
    }

    public static final Hash HashImpl = (v) -> v;

    public static int castToInt(final byte value) {
        return value;
    }

    public static byte castToByte(final int value) {
        return (byte) (value & 0xff);
    }

    public static byte unbox(final Object value) {
        return value != null ? ((Number) value).byteValue() : DEFAULT;
    }

    public static Byte box(final byte value) {
        return value;
    }

    public static byte parseString(final String value) {
        return Byte.parseByte(value);
    }

    public static byte convert(final Object value) {
        if (value == null) {
            return DEFAULT;
        }
        final Class<?> type = value.getClass();
        if (Number.class.isAssignableFrom(type)) {
            return ((Number) value).byteValue();
        }
        if (String.class.isAssignableFrom(type)) {
            return parseString((String) value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return (byte) (((Boolean) value) ? 1 : 0);
        }
        throw new IllegalArgumentException(
                String.format("Could not convert to byte: %s (%s)", value, type.getName()));
    }
}
