package com.bytefacets.collections.types;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressFBWarnings({"REC_CATCH_EXCEPTION", "DE_MIGHT_IGNORE"})
public final class ContentType {
    public static final NamedId NotSpecified = new NamedId(0, "NotSpecified");
    public static final NamedId Text = new NamedId(1, "Text");
    public static final NamedId Date = new NamedId(2, "Date");
    public static final NamedId Time = new NamedId(3, "Time");
    public static final NamedId DateTime = new NamedId(4, "DateTime");
    public static final NamedId Notional = new NamedId(5, "Notional");
    public static final NamedId Price = new NamedId(6, "Price");
    public static final NamedId Quantity = new NamedId(7, "Quantity");
    public static final NamedId Duration = new NamedId(8, "Duration");
    public static final NamedId Percent = new NamedId(9, "Percent");
    public static final NamedId Progress = new NamedId(10, "Progress");
    public static final NamedId Packed = new NamedId(11, "Packed");
    public static final NamedId Flag = new NamedId(12, "Flag");
    public static final NamedId Boolean = new NamedId(13, "Boolean");
    public static final NamedId Glyph = new NamedId(14, "Glyph");
    public static final NamedId Size = new NamedId(15, "Size");
    public static final NamedId Id = new NamedId(16, "Id");
    public static final NamedId Json = new NamedId(17, "Json");

    private static final Map<String, NamedId> registry = new HashMap<>();
    private static final NamedId[] names = new NamedId[32];

    static {
        Arrays.fill(names, NotSpecified);
        for (Field f : ContentType.class.getFields()) {
            final int mod = f.getModifiers();
            if (Modifier.isFinal(mod)
                    && Modifier.isPublic(mod)
                    && Modifier.isStatic(mod)
                    && f.getType().equals(NamedId.class)) {
                try {
                    final NamedId value = (NamedId) f.get(null);
                    names[value.id()] = value;
                    registry.put(f.getName(), value);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    private ContentType() {}

    public static NamedId normalize(final Object value) {
        if (value == null) {
            return NotSpecified;
        }
        if (value instanceof NamedId) {
            return (NamedId) value;
        }
        if (value instanceof Number) {
            return getById(((Number) value).intValue());
        }
        return getIdForName(value.toString());
    }

    public static NamedId getIdForName(final String name) {
        return registry.getOrDefault(name, NotSpecified);
    }

    public static NamedId getById(final int value) {
        if (value >= 0 && value < names.length) {
            return names[value];
        }
        return NotSpecified;
    }
}
