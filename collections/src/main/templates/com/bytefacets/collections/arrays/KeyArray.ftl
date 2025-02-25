<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.arrays;

import com.bytefacets.collections.types.${type.name}Type;

import java.util.Arrays;

/** Utility class for ${type.name} */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}Array {
    private ${type.name}Array() {

    }

<#if type.generic>
    /** Creates a new Object[] of the given size, but cast to T[]. */
    public static <T> T[] create(final int size) {
        return (T[]) new Object[size];
    }

    /** Creates a typed array of the given size using Array.newInstance. */
    public static <T> T[] create(final Class<T> type, final int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }

    /**
     * Creates a new Object[] array of the given size, and if the default value
     * is not null, fills the array with the value.
     */
    public static <T> T[] create(final int size, final T defaultValue) {
        final var array = new Object[size];
        if(!GenericType.EqImpl.areEqual(defaultValue, GenericType.DEFAULT)) {
            fill(array, defaultValue);
        }
        return (T[]) array;
    }
<#else>
    /** Creates a new ${type.arrayType}[] of the given size. */
    public static ${type.arrayType}[] create(final int size) {
        return new ${type.javaType}[size];
    }

    /**
     * Creates a new ${type.arrayType}[] of the given size, and if the value is
     * not the default, fills the array with the value.
     */
    public static ${type.arrayType}[] create(final int size, final ${type.javaType} defaultValue) {
        final var array = new ${type.javaType}[size];
        if(!${type.name}Type.EqImpl.areEqual(defaultValue, ${type.name}Type.DEFAULT)) {
            fill(array, defaultValue);
        }
        return array;
    }
</#if>
    /** Resizes the given array to the new size. There are no bounds checks. */
    public static ${generics} ${type.javaType}[] resize(final ${type.javaType}[] array, final int newSize) {
        final ${type.javaType}[] dest = create(newSize);
        System.arraycopy(array, 0, dest, 0, Math.min(array.length, newSize));
        return dest;
    }

    /** Fills the section of the array with the given value. There are no bounds checks. */
    public static ${generics} void fill(final ${type.javaType}[] array, final ${type.javaType} value, final int fromOffset, final int length) {
        for(int i = 0, offset = fromOffset; i < length; i++, offset++) {
            array[offset] = value;
        }   
    }

    /** Fills the array with the given value. */
    public static ${generics} void fill(final ${type.javaType}[] array, final ${type.javaType} value) {
        fill(array, value, 0, array.length);
    }

    /**
     * Creates a new array, copies the contents of the given array into the new one,
     * and fills the remainder with the default value.
     */
    public static ${generics} ${type.javaType}[] copyOf(final ${type.javaType}[] source, final int newLength, final ${type.javaType} defaultValue) {
        final ${type.javaType}[] newArray = Arrays.copyOf(source, newLength);
        fill(newArray, defaultValue, source.length, newArray.length - source.length);
        return newArray;
    }

    /** Creates a copy of the array using Arrays.copyOf. */
    public static ${generics} ${type.javaType}[] copyOf(final ${type.javaType}[] source, final int newLength) {
        return Arrays.copyOf(source, newLength);
    }

    /**
     * If the size is larger than the given array, a copy is created with a new size that can
     * accommodate the size. The growth formula is a doubling growth.
     */
    public static ${generics} ${type.javaType}[] ensureSize(final ${type.javaType}[] array, final int size) {
        if(size > array.length) {
            return copyOf(array, grow(size, array.length));
        }
        return array;
    }

    /**
     * If the size is larger than the given array, a copy is created with a new size that can
     * accommodate the size, and the new indexes are filled with the default value. The growth
     * formula is a doubling growth.
     */
    public static ${generics} ${type.javaType}[] ensureSize(final ${type.javaType}[] array, final int size, final ${type.javaType} initialValue) {
        if(size > array.length) {
            return copyOf(array, grow(size, array.length), initialValue);
        }
        return array;
    }

    /**
     * If the entry/index is not within the bounds of the given array, a copy is created with a
     * new size that can accommodate the entry. The growth formula is a doubling growth.
     */
    public static ${generics} ${type.javaType}[] ensureEntry(final ${type.javaType}[] array, final int entry) {
        return ensureSize(array, entry + 1);
    }

    /**
     * If the entry/index is not within the bounds of the given array, a copy is created with a
     * new size that can accommodate the entry, and the new indexes are filled with the default
     * value. The growth formula is a doubling growth.
     */
    public static ${generics} ${type.javaType}[] ensureEntry(final ${type.javaType}[] array, final int entry, final ${type.javaType} initialValue) {
        return ensureSize(array, entry + 1, initialValue);
    }

    /** Doubles the size until it accommodates the necessarySize. */
    private static int grow(final int necessarySize, final int currentLength) {
        int newLength = currentLength;
        while(newLength < necessarySize) {
            newLength <<= 1;
        }
        return newLength;
    }
}