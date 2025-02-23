package com.bytefacets.collections.vector;

import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.functional.${type.name}Consumer;
import com.bytefacets.collections.functional.${type.name}Iterable;


/**
 * This is a generated class. Changes to the class should be made on the template and the generator re-run.
 */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}Vector${generics} implements ${type.name}Iterable${generics} {
    private ${type.arrayType}[] values;
    private int size = 0;
    private int nextPos = 0;

    public ${type.name}Vector(final int initialCapacity) {
        if(initialCapacity < 1) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        values = ${type.name}Array.create(initialCapacity);
    }

    public void append(final ${type.javaType} value) {
        values = ${type.name}Array.ensureEntry(values, nextPos);
        values[nextPos++] = value;
        size++;
    }

    public ${type.javaType} valueAt(final int index) {
        return ${type.cast}values[index];
    }

    @Override
    public void forEach(final ${type.name}Consumer${generics} consumer) {
        for(int i = 0; i < size; i++) {
            consumer.accept(${type.cast}values[i]);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        <#if type.name == "Generic" || type.name == "String">
        ${type.name}Array.fill(values, null, 0, nextPos);
        </#if>
        nextPos = 0;
        size = 0;
    }
}