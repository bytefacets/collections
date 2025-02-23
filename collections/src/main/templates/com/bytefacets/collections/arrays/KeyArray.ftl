<#ftl strip_whitespace=true>
package com.bytefacets.collections.arrays;

import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

<#if type.isGeneric()>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}Array {
    private ${type.name}Array() {

    }

<#if type.isGeneric()>
    public static <T> T[] create(final int size) {
        return (T[]) new Object[size];
    }

    public static <T> T[] create(final Class<T> type, final int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }

    public static <T> T[] create(final int size, final T defaultValue) {
        final var array = new Object[size];
        if(!GenericType.EqImpl.areEqual(defaultValue, GenericType.DEFAULT)) {
            fill(array, defaultValue);
        }
        return (T[]) array;
    }
<#else>
    public static ${type.arrayType}[] create(final int size) {
        return new ${type.javaType}[size];
    }

    public static ${type.arrayType}[] create(final int size, final ${type.javaType} defaultValue) {
        final var array = new ${type.javaType}[size];
        if(!${type.name}Type.EqImpl.areEqual(defaultValue, ${type.name}Type.DEFAULT)) {
            fill(array, defaultValue);
        }
        return array;
    }
</#if>

    public static ${generics} ${type.javaType}[] resize(final ${type.javaType}[] array, final int newSize) {
        final ${type.javaType}[] dest = create(newSize);
        System.arraycopy(array, 0, dest, 0, Math.min(array.length, newSize));
        return dest;
    }

    public static ${generics} void fill(final ${type.javaType}[] array, final ${type.javaType} value, final int fromOffset, final int length) {
        for(int i = 0, offset = fromOffset; i < length; i++, offset++) {
            array[offset] = value;
        }   
    }

    public static ${generics} void fill(final ${type.javaType}[] array, final ${type.javaType} value) {
        fill(array, value, 0, array.length);
    }

    public static ${generics} ${type.javaType}[] copyOf(final ${type.javaType}[] source, final int newLength, final ${type.javaType} defaultValue) {
        final ${type.javaType}[] newArray = Arrays.copyOf(source, newLength);
        fill(newArray, defaultValue, source.length, newArray.length - source.length);
        return newArray;
    }

    public static ${generics} ${type.javaType}[] copyOf(final ${type.javaType}[] source, final int newLength) {
        return Arrays.copyOf(source, newLength);
    }

    public static ${generics} ${type.javaType}[] ensureSize(final ${type.javaType}[] array, final int size) {
        if(size > array.length) {
            return copyOf(array, grow(size, array.length));
        }
        return array;
    }
    
    public static ${generics} ${type.javaType}[] ensureSize(final ${type.javaType}[] array, final int size, final ${type.javaType} initialValue) {
        if(size > array.length) {
            return copyOf(array, grow(size, array.length), initialValue);
        }
        return array;
    }

    public static ${generics} ${type.javaType}[] ensureEntry(final ${type.javaType}[] array, final int entry) {
        return ensureSize(array, entry + 1);
    }

    public static ${generics} ${type.javaType}[] ensureEntry(final ${type.javaType}[] array, final int entry, final ${type.javaType} initialValue) {
        return ensureSize(array, entry + 1, initialValue);
    }

    private static int grow(final int necessarySize, final int currentLength) {
        int newLength = currentLength;
        while(newLength < necessarySize) {
            newLength <<= 1;
        }
        return newLength;
    }
}