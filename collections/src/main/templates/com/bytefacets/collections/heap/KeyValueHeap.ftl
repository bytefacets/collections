package com.bytefacets.collections.heap;

import com.bytefacets.collections.arrays.IntArray;
import com.bytefacets.collections.arrays.${key.name}Array;
import com.bytefacets.collections.arrays.${value.name}Array;
import com.bytefacets.collections.types.IntType;
import com.bytefacets.collections.types.${key.name}Type;
import com.bytefacets.collections.types.${value.name}Type;

/**
 * This is a generated class. Changes to the class should be made on the template and the generator re-run.
 */
<#if key.generic || value.generic>@SuppressWarnings("unchecked")</#if>
public class ${key.name}${value.name}Heap${generics} extends Base${key.name}Heap${key.declaration} {
    private ${value.arrayType} defaultValue = ${value.cast}${value.name}Type.DEFAULT;
    private ${value.arrayType}[] values;

    public ${key.name}${value.name}Heap(int initialCapacity) {
        super(initialCapacity);
        values = ${value.name}Array.create(initialCapacity, defaultValue);
    }

    public ${value.javaType} getDefaultValue() {
        return ${value.cast}defaultValue;
    }

    public void setDefaultValue(final ${value.javaType} defaultValue) {
        if(size > 0) {
            throw new RuntimeException("SetDefaultValue("+defaultValue+"): collection is not empty ("+getSize()+")" );
        }
        if(!${value.name}Type.EqImpl.areEqual(this.defaultValue, defaultValue)) {
            this.defaultValue = defaultValue;
            ${value.name}Array.fill(values, defaultValue, size, values.length);
        }
    }

    public int insert(final ${key.javaType} key, final ${value.javaType} value) {
        int entry = insert(key);
        values[entry] = value;
        return entry;
    }

    public ${value.javaType} getValueAt(final int entry) {
        return ${value.cast}values[entry];
    }

    public ${value.javaType} getValueAtOrDefault(final int entry, final ${value.javaType} defaultValue) {
        if(entry < 0) return defaultValue;
        if(entry >= values.length) return defaultValue;
        return ${value.cast}values[entry];
    }

    public void setValueAt(final int entry, final ${value.javaType} value) {
        values[entry] = value;
    }

    protected void growValues(final int size) {
        values = ${value.name}Array.copyOf(values, size, defaultValue);
    }

    protected void clearValueAt(final int entry) {
        values[entry] = defaultValue;
    }

    @Override
    protected void clearValues() {
        ${value.name}Array.fill(values, defaultValue, 0, size);
    }
}
