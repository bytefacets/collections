package com.bytefacets.collections.types;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Comparator;
import java.util.Objects;

public final class StringType {
    public static final String DEFAULT = null;

    private StringType() {}

    public interface Eq {
        boolean areEqual(String a, String b);
    }

    public static final Eq EqImpl = Objects::equals;

    public interface Cmp {
        int compare(String a, String b);
    }

    public static final Cmp Asc = (a, b) -> nullableCompare(a, b, String::compareTo);
    public static final Cmp Desc = (a, b) -> nullableCompare(b, a, String::compareTo);
    public static final Cmp AscInsensitive =
            (a, b) -> nullableCompare(a, b, String::compareToIgnoreCase);
    public static final Cmp DescInsensitive =
            (a, b) -> nullableCompare(b, a, String::compareToIgnoreCase);

    @SuppressWarnings("StringEquality")
    @SuppressFBWarnings(
            value = "ES_COMPARING_PARAMETER_STRING_WITH_EQ",
            justification = "this is a short-circuit for identity and null comparison")
    private static int nullableCompare(
            final String a, final String b, final Comparator<String> method) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        return method.compare(a, b);
    }

    public static Cmp comparatorFor(final CompareMethod method) {
        return switch (method) {
            case Ascending -> Asc;
            case Descending -> Desc;
            case AscendingAbsolute -> AscInsensitive;
            case DescendingAbsolute -> DescInsensitive;
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
    }

    public interface Pred {
        boolean test(String a, String b);
    }

    public static final Pred IS_EQ = Objects::equals;
    public static final Pred IS_NEQ = (a, b) -> !Objects.equals(a, b);
    public static final Pred IS_LT = (a, b) -> Asc.compare(a, b) < 0;
    public static final Pred IS_GT = (a, b) -> Asc.compare(a, b) > 0;
    public static final Pred IS_LTE = (a, b) -> Asc.compare(a, b) <= 0;
    public static final Pred IS_GTE = (a, b) -> Asc.compare(a, b) >= 0;

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
        int code(String v);
    }

    public static final Hash HashImpl = (v) -> v != null ? v.hashCode() : 0;

    public static int castToInt(final String value) {
        return Integer.parseInt(value);
    }

    public static String castToString(final int value) {
        return Integer.toString(value);
    }

    public static String unbox(final Object value) {
        return (String) value;
    }

    public static String box(final String value) {
        return value;
    }

    public static String parseString(final String value) {
        return value;
    }

    public static String convert(final Object value) {
        return value != null ? value.toString() : null;
    }
}
