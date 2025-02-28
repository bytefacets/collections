<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

/** A store of ${type.javaType}. */
public interface ${type.name}Store${generics} {
    /** Returns the value at the given index. */
    ${type.javaType} get${type.name}(int index);

    /** Sets the value at the given index. */
    void set${type.name}(int index, ${type.javaType} value);
}