<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

/** A store of ${type.javaType}. */
public interface Large${type.name}Store${generics} extends AutoCloseable {
    /** Returns the value at the given index. */
    ${type.javaType} get${type.name}(long index);

    /** Sets the value at the given index. */
    void set${type.name}(long index, ${type.javaType} value);

    /** Releases the memory for the store. */
    void close();
}