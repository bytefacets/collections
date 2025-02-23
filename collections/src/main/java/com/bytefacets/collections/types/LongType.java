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
